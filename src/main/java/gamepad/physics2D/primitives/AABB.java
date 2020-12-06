package gamepad.physics2D.primitives;

import gamepad.physics2D.rigidbody.Rigidbody2D;
import org.joml.Vector2f;

import javax.swing.*;

/**
 * Accessed aligned bounding box.
 */
public class AABB {

    private Rigidbody2D rigidbody2D = null;

    private Vector2f size = new Vector2f();
    private Vector2f halfSize;

    public AABB() {
        this.halfSize = new Vector2f(size).div(2f);
    }

    public AABB(Vector2f min, Vector2f max) {
        this.size = new Vector2f(max).sub(min);
        this.halfSize = new Vector2f(size).div(2f);
    }

    public Vector2f getMin() {
        return new Vector2f(this.rigidbody2D.getPosition()).sub(this.halfSize);
    }

    public Vector2f getMax() {
        return new Vector2f(this.rigidbody2D.getPosition()).add(this.halfSize);
    }

}
