package gamepad.physics2D.primitives;

import gamepad.physics2D.rigidbody.Rigidbody2D;
import org.joml.Vector2f;

public class Box2D {

    private Rigidbody2D rigidbody2D = null;

    private Vector2f size = new Vector2f();
    private Vector2f halfSize = new Vector2f();

    public Box2D() {
        this.halfSize = new Vector2f(size).div(2f);
    }

    public Box2D(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size).div(2f);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidbody2D.getPosition()).sub(this.halfSize);
    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidbody2D.getPosition()).add(this.halfSize);
    }

    public Vector2f[] getVertices() {
        Vector2f min = this.getMin();
        Vector2f max = this.getMax();

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y),
                new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y),
                new Vector2f(max.x, max.y)
        };

        if(rigidbody2D.getRotation() != 0.0f) {
            for(Vector2f vertex : vertices) {
                // TODO: Implement
                // Rotates point(Vector2f) about center(Vector2f) by rotation(float in degrees).
//                GMath.rotate(vertex, this.rigidbody2D.getPosition(), this.rigidbody2D.getRotation());
            }
        }

        return vertices;
    }
}
