package gamepad.renderer;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Line2D {

    private Vector2f start;
    private Vector2f end;
    private Vector3f color;
    private int lifeTime;

    public Line2D(Vector2f start, Vector2f end) {
        this.start = start;
        this.end = end;
    }

    public Line2D(Vector2f start, Vector2f end, Vector3f color, int lifeTime) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.lifeTime = lifeTime;
    }

    public int beginFrame() {
        this.lifeTime--;
        return this.lifeTime;
    }

    public Vector2f getStart() {
        return start;
    }
    public Vector2f getEnd() {
        return end;
    }
    public Vector3f getColor() {
        return color;
    }
}
