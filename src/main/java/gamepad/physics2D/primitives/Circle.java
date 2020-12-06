package gamepad.physics2D.primitives;

import gamepad.physics2D.rigidbody.Rigidbody2D;
import org.joml.Vector2f;

public class Circle {

    private Rigidbody2D rigidbody2D = null;

    private float radius = 1.0f;

    public float getRadius() { return this.radius; }

    public Vector2f getCenter() { return this.rigidbody2D.getPosition(); }

}
