package gamepad.physics2D.rigidbody;

import gamepad.object.components.Component;
import gamepad.utils.Transform;
import org.joml.Vector2f;

public class Rigidbody2D extends Component {

    private Transform rawTransform;

    private Vector2f forceAccum = new Vector2f();
    private Vector2f position = new Vector2f();
    private float rotation = 0.0f;
    private float mass = 0.0f;
    private float inverseMass = 0.0f;

    private Vector2f linearVelocity = new Vector2f();
    private float angularVelocity = 0.0f;
    private float linearDamping = 0.0f;
    private float angularDamping = 0.0f;

    public Vector2f getPosition() { return this.position; }
    public float getRotation() { return this.rotation; }

    public void physicsUpdate(float deltaTime) {
        if(this.mass == 0.0f) return;

        // Calculate linear velocity
        Vector2f acceleration = new Vector2f(forceAccum).mul(this.inverseMass);
        linearVelocity.add(acceleration.mul(deltaTime));

        // Update the linear position
        this.position.add(new Vector2f(linearVelocity).mul(deltaTime));

        this.syncCollisionTransforms();
        this.clearAccumulators();
    }

    public void syncCollisionTransforms() {
        if(rawTransform != null) {
            rawTransform.position.set(this.position);
        }
    }

    public void clearAccumulators() {
        this.forceAccum.zero();
    }

    public float getMass() { return mass; }

    public void setMass(float mass) {
        this.mass = mass;
        if(this.mass != 0.0f) {
            this.inverseMass = 1.0f / this.mass;
        }
    }

    public void addForce(Vector2f force) {
        this.forceAccum.add(force);
    }

    public void setRawTransform(Transform rawTransform) {
        this.rawTransform = rawTransform;
        this.position.set(rawTransform.position);
    }
}
