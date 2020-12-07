package gamepad.physics2D.forces;

import gamepad.physics2D.rigidbody.Rigidbody2D;
import org.joml.Vector2f;

public class Gravity2D implements ForceGenerator {

    private Vector2f gravity;

    public Gravity2D(Vector2f force) {
        this.gravity = new Vector2f(force);
    }

    @Override
    public void updateForce(Rigidbody2D body, float deltaTime) {
        body.addForce(new Vector2f(gravity).mul(body.getMass()));
    }
}
