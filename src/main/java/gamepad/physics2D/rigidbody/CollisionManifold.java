package gamepad.physics2D.rigidbody;

import org.joml.Vector2f;

import java.util.ArrayList;

public class CollisionManifold {

    private Vector2f normal;
    private ArrayList<Vector2f> contactPoints;
    private boolean isColliding;
    private float depth;

    public CollisionManifold() {
        normal = new Vector2f();
        depth = 0.0f;
    }

    public CollisionManifold(Vector2f normal, float depth) {
        this.normal = normal;
        this.contactPoints = new ArrayList<>();
        this.depth = depth;
        this.isColliding = true;
    }

    public void addContactPoint(Vector2f contact) {
        this.contactPoints.add(contact);
    }

    public Vector2f getNormal() {
        return normal;
    }

    public ArrayList<Vector2f> getContactPoints() {
        return contactPoints;
    }

    public float getDepth() {
        return depth;
    }
}
