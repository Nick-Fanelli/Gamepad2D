package gamepad.physics2D.rigidbody;

import gamepad.physics2D.primitives.AABB;
import gamepad.physics2D.primitives.Box2D;
import gamepad.physics2D.primitives.Circle;
import gamepad.renderer.Line2D;
import gamepad.utils.PhysicsMath;
import org.joml.Vector2f;

/**
 * The type Intersection Detector 2D is used to detect intersections and collisions with
 * any Physics2D objects.
 *
 * @author Nick Fanelli
 * @since 6-Dec-2020
 * @version 1.0
 */
public class IntersectionDetector2D {

    // ==========================================
    // Point vs. Primitive Test
    // ==========================================
    /**
     * Returns if a point is intersecting a line.
     *
     * @param point the point to be checked
     * @param line  the line to be checked
     * @return the boolean whether or not it intersects
     */
    public static boolean pointOnLine(Vector2f point, Line2D line) {
        float dy = line.getEnd().y = line.getStart().y;
        float dx = line.getEnd().x = line.getStart().x;
        float slope = dy / dx;

        float yIntercept = line.getEnd().y - (slope * line.getEnd().x);

        // Check the line equation
        return point.y == (slope * point.x + yIntercept);
    }

    /**
     * Returns if a point is intersecting a circle
     *
     * @param point  the point to be checked
     * @param circle the circle to be checked
     * @return the boolean whether or not it intersects
     */
    public static boolean pointInCircle(Vector2f point, Circle circle) {
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToPoint = new Vector2f(point.sub(circleCenter));

        return centerToPoint.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    /**
     * Returns if a point is intersecting an AABB.
     *
     * @param point the point to be checked
     * @param box   the box to be checked
     * @return the boolean whether or not it intersects
     */
    public static boolean pointInAABB(Vector2f point, AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return point.x <= max.x && min.x <= point.x &&
                point.y <= max.y && min.y <= point.y;
    }

    /**
     * Returns if a point is intersecting a Box2D.
     *
     * @param point the point to be checked
     * @param box   the box to be checked
     * @return the boolean whether or not it intersects
     */
    public static boolean pointInBox2D(Vector2f point, Box2D box) {
        // Translate the point into local space
        Vector2f pointLocalBoxSpace = new Vector2f(point);
        PhysicsMath.rotate(pointLocalBoxSpace, box.getRigidbody2D().getRotation(),
                box.getRigidbody2D().getPosition());

        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        return pointLocalBoxSpace.x <= max.x && min.x <= pointLocalBoxSpace.x &&
                pointLocalBoxSpace.y <= max.y && min.y <= pointLocalBoxSpace.y;
    }

    // ==========================================
    // Line vs. Primitive Tests
    // ==========================================

}
