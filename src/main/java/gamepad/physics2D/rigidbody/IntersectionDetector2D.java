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
 * @version 1.0
 * @since 6 -Dec-2020
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
        float dy = line.getEnd().y - line.getStart().y;
        float dx = line.getEnd().x - line.getStart().x;

        // Catch vertical lines
        if (dx == 0f) {
            return PhysicsMath.compare(point.x, line.getStart().x);
        }

        float m = dy / dx;

        float b = line.getEnd().y - (m * line.getEnd().x);

        // Check the line equation
        return point.y == m * point.x + b;
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
        Vector2f centerToPoint = new Vector2f(point).sub(circleCenter);

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
    /**
     * Returns if a line is intersecting a circle
     *
     * @param line   the line to be checked
     * @param circle the circle to be checked
     * @return the boolean whether or not it intersects
     */
    public static boolean lineAndCircle(Line2D line, Circle circle) {
        if (pointInCircle(line.getStart(), circle) || pointInCircle(line.getEnd(), circle)) {
            return true;
        }

        Vector2f ab = new Vector2f(line.getEnd()).sub(line.getStart());

        // Project point (circle position) onto ab (line segment)
        // parameterized position t
        Vector2f circleCenter = circle.getCenter();
        Vector2f centerToLineStart = new Vector2f(circleCenter).sub(line.getStart());
        float t = centerToLineStart.dot(ab) / ab.dot(ab);

        if (t < 0.0f || t > 1.0f) {
            return false;
        }

        // Find the closest point to the line segment
        Vector2f closestPoint = new Vector2f(line.getStart()).add(ab.mul(t));

        return pointInCircle(closestPoint, circle);
    }
}
