package gamepad.physics2D;

import gamepad.physics2D.forces.ForceRegistry;
import gamepad.physics2D.forces.Gravity2D;
import gamepad.physics2D.rigidbody.Rigidbody2D;
import org.joml.Vector2f;

import java.util.ArrayList;

public class PhysicsSystem2D {

    private ForceRegistry forceRegistry;
    private ArrayList<Rigidbody2D> rigidbodies;
    private Gravity2D gravity;
    private float fixedUpdate;

    public PhysicsSystem2D(float fixedDeltaTime, Vector2f gravity) {
        this.forceRegistry = new ForceRegistry();
        this.rigidbodies = new ArrayList<>();
        this.gravity = new Gravity2D(gravity);
        this.fixedUpdate = fixedDeltaTime;
    }

    public void update(float deltaTime) {
        this.fixedUpdate();
    }

    public void fixedUpdate() {
        forceRegistry.updateForces(fixedUpdate);

        // Update the velocities of all rigidbodies
        for(int i = 0; i < rigidbodies.size(); i++) {
            rigidbodies.get(i).physicsUpdate(fixedUpdate);
        }
    }

    public void addRigidbody(Rigidbody2D body) {
        this.rigidbodies.add(body);
        this.forceRegistry.add(body, gravity);
    }

}
