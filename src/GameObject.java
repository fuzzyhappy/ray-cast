import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    float x;
    float y;
    private BufferedImage sprite;

    public GameObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Shape render() {
        return new Rectangle2D.Float(0, 0, 0, 0);
    }
}
