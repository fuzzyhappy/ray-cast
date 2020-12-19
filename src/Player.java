import java.awt.geom.Ellipse2D;

public class Player implements Constants {
    public double x;
    public double y;
    public double vx;
    public double vy;
    public double angle;

    public final byte[][] map;

    public Player(double x, double y, byte[][] map) {
        this.x = x;
        this.y = y;
        this.map = map;
        vx = 0;
        vy = 0;
        angle = 0;
    }

    public void normalizeVelocity() {
        double cv = Math.sqrt(vx * vx + vy * vy);
        if (cv == 0) return;
        vx = vx / cv * v;
        vy = vy / cv * v;
    }

    private double stepX() {
        if (x + vx >= map[0].length * side) {
            return 0;
        }
        if (x + vx < 0) {
            return map[0].length * side - 1;
        }
        return x + vx;
    }

    private double stepY() {
        if (y + vy >= map.length * side) {
            return 0;
        }
        if (y + vy < 0) {
            return map.length * side - 1;
        }
        return y + vy;
    }

    private double edgeX() {
        double distanceToTop = (int)(x / side) * side - x;
        double distanceToBottom = (int)(x / side + 1) * side - x;
        return (Math.abs(distanceToTop) > Math.abs(distanceToBottom) ?
                distanceToBottom - 1 : distanceToTop + 1);
    }

    private double edgeY() {
        double distanceToLeft = (int)(y / side) * side - y;
        double distanceToRight = (int)(y / side + 1) * side - y;
        return (Math.abs(distanceToLeft) > Math.abs(distanceToRight) ?
                distanceToRight - 1 : distanceToLeft + 1);
    }

    private void screenWrap() {
        if (x >= map[0].length * side) {
            x = 0;
        }
        if (x < 0) {
            x = map[0].length * side - 1;
        }
        if (y >= map.length * side) {
            y = 0;
        }
        if (y < 0) {
            y = map.length * side - 1;
        }
    }

    public void update() {
        normalizeVelocity();
        //screenWrap();

        if (map[(int)(y/side)][(int)(stepX()/side)] == 1) {
            vx = edgeX();
        }
        if (map[(int)(stepY()/side)][(int)(x/side)] == 1) {
            vy = edgeY();
        }

        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        angle %= 2 * Math.PI;

        x = stepX();
        y = stepY();

        vx = 0;
        vy = 0;
    }
}
