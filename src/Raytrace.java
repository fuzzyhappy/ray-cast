import java.util.Scanner;

public class Raytrace implements Constants {
    public double x;
    public double y;
    public double angle;
    public double distX;
    public double distY;
    public final byte[][] map;

    public Raytrace(double x, double y, double angle, byte[][] map) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.map = map;
        distX = x;
        distY = y;
    }

    public double raytrace() {
        boolean hit = false;
        double dist = 0;
        double cx = x;
        double cy = y;
        double dx = 0;
        double dy = 0;
        while (!hit) {
            try {
                if (angle > Math.PI / 2 && angle <= 3 * Math.PI / 2) {
                    if (cx % side == 0) {
                        dx = ((int)(cx / side - 1) * side - cx) / Math.cos(angle);
                    } else {
                        dx = ((int)(cx / side) * side - cx) / Math.cos(angle);
                    }
                } else if (angle <= Math.PI / 2 || angle > 3 * Math.PI / 2) {
                    dx = ((int)(cx / side + 1) * side - cx) / Math.cos(angle);
                }
            } catch (ArithmeticException e) {
                dx = Double.MAX_VALUE;
            }
            try {
                if (angle >= 0 && angle < Math.PI) {
                    dy = ((int)(cy / side + 1) * side - cy) / Math.sin(angle);
                }
                if (angle >= Math.PI && angle < 2 * Math.PI) {
                    if (cy % side == 0) {
                        dy = ((int)(cy / side - 1) * side - cy) / Math.sin(angle);
                    } else {
                        dy = ((int)(cy / side) * side - cy) / Math.sin(angle);
                    }
                }
            } catch (ArithmeticException e) {
                dy = Double.MAX_VALUE;
            }
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
            dist = Math.sqrt((distX - x) * (distX - x) + (distY - y) * (distY - y));
            if (dist > side * 14) {
                hit = true;
            }
            if (dx < dy) {
                if (angle > Math.PI / 2 && angle <= 3 * Math.PI / 2) {
                    if (map[(int)(cy / side)][indexStepX(cx, -1)] == 1) {
                        hit = true;
                    }
                }
                if (angle <= Math.PI / 2 || angle > 3 * Math.PI / 2) {
                    if (map[(int)(cy / side)][(int)(cx / side)] == 1) {
                        hit = true;
                    }
                }
            } else if (dy <= dx) {
                if (angle >= 0 && angle < Math.PI) {
                    if (map[(int)(cy / side)][(int)(cx / side)] == 1) {
                        hit = true;
                    }
                }
                if (angle >= Math.PI && angle < 2 * Math.PI) {
                    if (map[indexStepY(cy, -1)][(int)(cx / side)] == 1) {
                        hit = true;
                    }
                }
            }
        }
        return dist;
    }

    public double stepX(double cx, double d) {
        distX += d * Math.cos(angle);
        if (cx + d * Math.cos(angle) >= map[0].length * side) {
            return (cx + d * Math.cos(angle)) % (map[0].length * side);
        } else if (cx + d * Math.cos(angle) < 0) {
            return side * map[0].length + cx + d * Math.cos(angle);
        }
        return cx + d * Math.cos(angle);
    }

    public double stepY(double cy, double d) {
        distY += d * Math.sin(angle);
        if (cy + d * Math.sin(angle) >= map.length * side) {
            return (cy + d * Math.sin(angle)) % (map.length * side);
        } else if (cy + d * Math.sin(angle) < 0) {
            return side * map.length + cy + d * Math.sin(angle);
        }
        return cy + d * Math.sin(angle);
    }

    public int indexStepX(double cx, int step) {
        int index = (int)(cx / side) + step;
        if (index < 0) {
            return map[0].length + index;
        } else if (index >= map[0].length) {
            return index % map[0].length;
        }
        return index;
    }

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
        Raytrace ray = new Raytrace(60, 20, Math.PI - Math.PI / 4, map);
        ray.raytrace();
    }
}
