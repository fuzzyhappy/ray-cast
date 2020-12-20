/**
 * Represents a ray. Raycasting algorithm is based off of DDA.
 *
 * @author Evan Wang
 * @version 2020.12.19
 */
public class Raycast implements Constants {
    public double x;            // x-coordinate of the ray's point origin
    public double y;            // y-coordinate of the ray's point origin
    public double angle;        // angled orientation of the ray
    public double distX;        // accumulated displacement of the endpoint of the ray along x-axis
    public double distY;        // accumulated displacement of the endpoint of the ray along y-axis
    public final byte[][] map;  // map the ray is in

    /**
     * Constructor of the raycast.
     *
     * @param x x-coordinate of the ray's point origin
     * @param y y-coordinate of the ray's point origin
     * @param angle angled orientation of the ray
     * @param map map the ray is inside
     */
    public Raycast(double x, double y, double angle, byte[][] map) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.map = map;
        distX = x;
        distY = y;
    }

    /**
     * Performs the raycast operation. Tracks absolute distance and not
     * perpendicular distance to the hit wall.
     *
     * NOTE: a side effect of how the map is represented is that each grid
     * tile's sides range between [0, max).
     *
     * @return RaycastCache containing raycast data
     */
    public RaycastCache raycast() {
        boolean hit = false;
        double dist = 0;
        int texture = 0;
        // current coordinates of the ray's endpoint
        double cx = x;
        double cy = y;
        // displacement between the ray's current endpoint and the closest grid edge
        double dx = 0;
        double dy = 0;

        // while the ray has not hit a wall
        while (!hit) {

            try {
                // if the x-direction of the ray is negative
                if (angle > Math.PI / 2 && angle <= 3 * Math.PI / 2) {
                    // if the ray's endpoint is on the x = 0 edge of a tile
                    if (cx % side == 0) {
                        dx = ((int)(cx / side - 1) * side - cx) / Math.cos(angle);
                    } else {
                        dx = ((int)(cx / side) * side - cx) / Math.cos(angle);
                    }
                // if the x-direction of the ray is positive
                } else if (angle <= Math.PI / 2 || angle > 3 * Math.PI / 2) {
                    dx = ((int)(cx / side + 1) * side - cx) / Math.cos(angle);
                }
            } catch (ArithmeticException e) {
                // if cos(angle) = 0
                dx = Double.MAX_VALUE;
            }

            try {
                // if the x-direction of the ray is positive
                if (angle >= 0 && angle < Math.PI) {
                    dy = ((int)(cy / side + 1) * side - cy) / Math.sin(angle);
                }
                // if the x-direction of the ray is negative
                if (angle >= Math.PI && angle < 2 * Math.PI) {
                    // if the ray's endpoint is on the y = 0 edge of a tile
                    if (cy % side == 0) {
                        dy = ((int)(cy / side - 1) * side - cy) / Math.sin(angle);
                    } else {
                        dy = ((int)(cy / side) * side - cy) / Math.sin(angle);
                    }
                }
            } catch (ArithmeticException e) {
                // if sin(angle) = 0
                dy = Double.MAX_VALUE;
            }

            // calculates what direction to move the ray's endpoint in
            // prioritizes the shortest time to an edge
            if (dy < dx) {
                cx = stepX(cx, dy);
                cy = stepY(cy, dy);
            } else if (dx < dy) {
                cx = stepX(cx, dx);
                cy = stepY(cy, dx);
            } else {
                cx = stepX(cx, dx);
                cy = stepY(cy, dy);
            }

            // calculates the distance the ray's endpoint has currently travelled
            dist = Math.sqrt((distX - x) * (distX - x) + (distY - y) * (distY - y));

            // edge case for endless corridor
            if (dist >= side * side && (Math.abs(Math.cos(angle)) <= sigma || Math.abs(Math.sin(angle)) <= sigma)) {
                hit = true;
                texture = -1;
            }

            // if the ray is prioritizing an x-side
            if (dx < dy) {

                // if the x-direction of the ray is negative, check the tile to the left
                if (angle > Math.PI / 2 && angle <= 3 * Math.PI / 2) {
                    if (map[indexStepY(cy, 0)][indexStepX(cx, -1)] != 0) {
                        hit = true;
                        texture = map[indexStepY(cy, 0)][indexStepX(cx, -1)];
                    }
                }

                // if the x-direction of the ray is positive, check the tile to the right
                if (angle <= Math.PI / 2 || angle > 3 * Math.PI / 2) {
                    if (map[indexStepY(cy, 0)][indexStepX(cx, 0)] != 0) {
                        hit = true;
                        texture = map[indexStepY(cy, 0)][indexStepX(cx, 0)];
                    }
                }

            // if the ray is prioritizing a y-side
            } else if (dy <= dx) {
                // if the y-direction of the ray is positive, check the tile to the top
                if (angle >= 0 && angle < Math.PI) {
                    if (map[indexStepY(cy, 0)][indexStepX(cx, 0)] != 0) {
                        hit = true;
                        texture = map[indexStepY(cy, 0)][indexStepX(cx, 0)];
                    }
                }

                // if the y-direction of the ray is negative, check the tile to the bottom
                if (angle >= Math.PI && angle < 2 * Math.PI) {
                    if (map[indexStepY(cy, -1)][indexStepX(cx, 0)] != 0) {
                        hit = true;
                        texture = map[indexStepY(cy, -1)][indexStepX(cx, 0)];
                    }
                }
            }
        }
        return new RaycastCache(texture, dist);
    }

    /**
     * Method for incrementing the x-coordinate of the ray's endpoint.
     * Accounts for wrapping around the map.
     *
     * @param cx, current x-coordinate of the ray's endpoint
     * @param d, time to spend travelling
     * @return the new x-coordinate of the ray's endpoint
     */
    public double stepX(double cx, double d) {
        distX += d * Math.cos(angle);
        if (cx + d * Math.cos(angle) >= map[0].length * side) {
            return (cx + d * Math.cos(angle)) % (map[0].length * side);
        } else if (cx + d * Math.cos(angle) < 0) {
            return side * map[0].length + cx + d * Math.cos(angle);
        }
        return cx + d * Math.cos(angle);
    }

    /**
     * Method for incrementing the y-coordinate of the ray's endpoint.
     * Accounts for wrapping around the map.
     *
     * @param cy, current y-coordinate of the ray's endpoint
     * @param d, time to spend travelling
     * @return the new y-coordinate of the ray's endpoint
     */
    public double stepY(double cy, double d) {
        distY += d * Math.sin(angle);
        if (cy + d * Math.sin(angle) >= map.length * side) {
            return (cy + d * Math.sin(angle)) % (map.length * side);
        } else if (cy + d * Math.sin(angle) < 0) {
            return side * map.length + cy + d * Math.sin(angle);
        }
        return cy + d * Math.sin(angle);
    }

    /**
     * Method for incrementing a map array index in the x-direction.
     * Accounts for wrapping around the array
     *
     * @param cx, current x index
     * @param step, x increment
     * @return the new x index
     */
    public int indexStepX(double cx, int step) {
        int index = (int)(cx / side) + step;
        if (index < 0) {
            return map[0].length + index;
        } else if (index >= map[0].length) {
            return index % map[0].length;
        }
        return index;
    }

    /**
     * Method for incrementing a map array index in the y-direction.
     * Accounts for wrapping around the array
     *
     * @param cy, current x index
     * @param step, y increment
     * @return the new y index
     */
    public int indexStepY(double cy, int step) {
        int index = (int)(cy / side) + step;
        if (index < 0) {
            return map.length + index;
        } else if (index >= map.length) {
            return index % map.length;
        }
        return index;
    }

    public static void main(String[] args) {
        byte[][] map = {{1, 0, 0, 1},
                {1, 0, 1, 0},
                {1, 0, 0, 0}};
        Raycast ray = new Raycast(60, 20, Math.PI - Math.PI / 4, map);
        ray.raycast();
    }
}
