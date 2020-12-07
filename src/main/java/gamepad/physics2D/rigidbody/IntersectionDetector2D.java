package gamepad.physics2D.rigidbody;

import gamepad.physics2D.RaycastResult;
import gamepad.physics2D.primitives.AABB;
import gamepad.physics2D.primitives.Box2D;
import gamepad.physics2D.primitives.Circle;
import gamepad.physics2D.primitives.Ray2D;
import gamepad.renderer.Line2D;
import gamepad.utils.PhysicsMath;
import org.joml.Vector2f;

import javax.swing.*;

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

    /**
     * Returns if a line is intersecting an AABB Box
     *
     * @param line the line to be checked
     * @param box  the box to be checked
     * @return the boolean whether or not it is intersecting
     */
    public static boolean lineAndAABB(Line2D line, AABB box) {
        if(pointInAABB(line.getStart(), box) || pointInAABB(line.getEnd(), box)) return true;

        Vector2f unitVector = new Vector2f(line.getEnd()).sub(line.getStart());
        unitVector.normalize();

        unitVector.x = (unitVector.x != 0) ? 1.0f / unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0) ? 1.0f / unitVector.y : 0f;

        Vector2f min = box.getMin();
        min.sub(line.getStart()).mul(unitVector);

        Vector2f max = box.getMax();
        max.sub(line.getStart()).mul(unitVector);

        float tMin = Math.max(Math.min(min.x, max.y), Math.min(min.y, max.y));
        float tMax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));
        if(tMax < 0 || tMin > tMax) return false;

        float t = (tMin < 0f) ? tMax : tMin;
        return t > 0f && t * t < line.lengthSquared();
    }

    /**
     * Return is a line is intersecting with a Box2D.
     *
     * @param line the line to be checked
     * @param box  the box to be checked
     * @return the boolean whether or not it is intersecting
     */
    public static boolean lineAndBox2D(Line2D line, Box2D box) {
        float theta = -box.getRigidbody2D().getRotation();

        Vector2f center = box.getRigidbody2D().getPosition();
        Vector2f localStart = new Vector2f(line.getStart());
        Vector2f localEnd = new Vector2f(line.getEnd());

        PhysicsMath.rotate(localStart, theta, center);
        PhysicsMath.rotate(localEnd, theta, center);

        Line2D localLine = new Line2D(localStart, localEnd);
        AABB aabb = new AABB(box.getMin(), box.getMax());

        return lineAndAABB(localLine, aabb);
    }

    /**
     * Makes a raycast from the specified ray and detects if it intersects with circle.
     *
     * @param circle the target
     * @param ray    the ray that will be casted
     * @param result the result that will be returned with detail
     * @return the boolean whether or not the raycast hit the target
     */
// Raycasts
    public static boolean raycast(Circle circle, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f originToCircle = new Vector2f(circle.getCenter()).sub(ray.getOrigin());
        float radiusSquared = circle.getRadius() * circle.getRadius();
        float originToCircleLengthSquared = originToCircle.lengthSquared();

        // Project the vector form the ray origin onto the direction of the ray
        float a = originToCircle.dot(ray.getDirection());
        float bSq = originToCircleLengthSquared - (a * a);
        if(radiusSquared - bSq < 0.0f) {
            return false;
        }

        float f = (float) Math.sqrt(radiusSquared - bSq);
        float t = 0;
        if(originToCircleLengthSquared < radiusSquared) { // Ray starts inside the circle
            t = a + f;
        } else {
            t = a - f;
        }

        if(result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(point).sub(circle.getCenter());
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    /**
     * Makes a raycast from the specified ray and detects if it intersects with an AABB box.
     *
     * @param box    the target
     * @param ray    the ray that will be casted
     * @param result the result that will be returned with detail
     * @return the boolean whether or not the raycast hit the target
     */
    public static boolean raycast(AABB box, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f unitVector = ray.getDirection();
        unitVector.normalize();

        unitVector.x = (unitVector.x != 0) ? 1.0f / unitVector.x : 0f;
        unitVector.y = (unitVector.y != 0) ? 1.0f / unitVector.y : 0f;

        Vector2f min = box.getMin();
        min.sub(ray.getOrigin()).mul(unitVector);

        Vector2f max = box.getMax();
        max.sub(ray.getOrigin()).mul(unitVector);

        float tMin = Math.max(Math.min(min.x, max.y), Math.min(min.y, max.y));
        float tMax = Math.min(Math.max(min.x, max.x), Math.max(min.y, max.y));
        if(tMax < 0 || tMin > tMax) return false;

        float t = (tMin < 0f) ? tMax : tMin;
        boolean hit = t > 0f; // && t * t < ray.getMaximum();
        if(!hit) return false;

        if(result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    /**
     * Makes a raycast from the specified ray and detects if it intersects with a Box2D.
     *
     * @param box    the target
     * @param ray    the ray that will be created
     * @param result the result that will be returned with detail
     * @return the boolean whether or not the raycast hit the target
     */
    public static boolean raycast(Box2D box, Ray2D ray, RaycastResult result) {
        RaycastResult.reset(result);

        Vector2f size = box.getHalfSize();

        Vector2f xAxis = new Vector2f(1, 0);
        Vector2f yAxis = new Vector2f(0, 1);

        PhysicsMath.rotate(xAxis, -box.getRigidbody2D().getRotation(), new Vector2f(0, 0));
        PhysicsMath.rotate(yAxis, -box.getRigidbody2D().getRotation(), new Vector2f(0, 0));

        Vector2f p = new Vector2f(box.getRigidbody2D().getPosition()).sub(ray.getOrigin());
        // Project the direction of the ray onto each axis of the box
        Vector2f f = new Vector2f(xAxis.dot(ray.getDirection()), yAxis.dot(ray.getDirection()));
        // Project p onto every axis of the box
        Vector2f e = new Vector2f(xAxis.dot(p), yAxis.dot(p));

        float[] tArray = { 0, 0, 0, 0};

        for(int i = 0; i < 2; i++) {
            if(PhysicsMath.compare(f.get(i), 0)) {
                // If the ray is parallel to the current axis, and the origin of the ray
                // is not inside, there is have no intersection
                if(-e.get(i) - size.get(i) > 0 || -e.get(i) + size.get(i) < 0) return false;

                f.setComponent(i, 0.00001f); // Set it to small value, to avoid divide by zero.
            }
            tArray[i * 2]     = (e.get(i) + size.get(i)) / f.get(i); // tMax for the axis
            tArray[i * 2 + 1] = (e.get(i) - size.get(i)) / f.get(i); // tMin for the axis
        }

        float tMin = Math.max(Math.min(tArray[0], tArray[1]), Math.min(tArray[2], tArray[3]));
        float tMax = Math.min(Math.max(tArray[0], tArray[1]), Math.max(tArray[2], tArray[3]));

        float t = (tMin < 0f) ? tMax : tMin;
        boolean hit = t > 0f; // && t * t < ray.getMaximum();
        if(!hit) return false;

        if(result != null) {
            Vector2f point = new Vector2f(ray.getOrigin()).add(new Vector2f(ray.getDirection()).mul(t));
            Vector2f normal = new Vector2f(ray.getOrigin()).sub(point);
            normal.normalize();

            result.init(point, normal, t, true);
        }

        return true;
    }

    // ============================================
    // Circle vs. Primitive Tests
    // ============================================

    /**
     * Returns if a circle is intersecting with a line.
     *
     * @param circle the circle to be checked
     * @param line   the line to be checked
     * @return the boolean whether or not it intersects
     */
    public static boolean circleAndLine(Circle circle, Line2D line) { return lineAndCircle(line, circle); }

    /**
     * Returns if a circle is intersecting with another circle.
     *
     * @param c1 the first circle to be checked
     * @param c2 the second circle to be checked
     * @return the boolean whether or not the circles intersect
     */
    public static boolean circleAndCircle(Circle c1, Circle c2) {
        Vector2f vecBetweenCenters = new Vector2f(c1.getCenter()).sub(c2.getCenter());
        float radiiSum = c1.getRadius() + c2.getRadius();
        return vecBetweenCenters.lengthSquared() <= radiiSum * radiiSum;
    }

    /**
     * Returns if a circle is intersecting with an AABB box.
     *
     * @param circle the circle to be checked
     * @param box    the box to be checked
     * @return the boolean whether or not the circles intersects
     */
    public static boolean circleAndAABB(Circle circle, AABB box) {
        Vector2f min = box.getMin();
        Vector2f max = box.getMax();

        Vector2f closestPointToCircle = new Vector2f(circle.getCenter());

        if(closestPointToCircle.x < min.x) {
            closestPointToCircle.x = min.x;
        } else if(closestPointToCircle.x > max.x) {
            closestPointToCircle.x = max.x;
        }

        if(closestPointToCircle.y < min.y) {
            closestPointToCircle.y = min.y;
        } else if(closestPointToCircle.y > max.y) {
            closestPointToCircle.y = max.y;
        }

        Vector2f circleToBox = new Vector2f(circle.getCenter()).sub(closestPointToCircle);
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    /**
     * Returns if a circle is intersecting with a Box2D
     *
     * @param circle the circle to be checked
     * @param box    the box to be checked
     * @return the boolean whether or not the circle intersects
     */
    public static boolean circleAndBox2D(Circle circle, Box2D box) {
        // Treat the box just like an AABB, after we rotate the stuff
        Vector2f min = new Vector2f();
        Vector2f max = new Vector2f(box.getHalfSize()).mul(2.0f);

        // Create a circle in box's local space.
        Vector2f r = new Vector2f(circle.getCenter()).sub(box.getRigidbody2D().getPosition());
        PhysicsMath.rotate(r, -box.getRigidbody2D().getRotation(), new Vector2f(0f, 0f));
        Vector2f localCirclePos = new Vector2f(r).add(box.getHalfSize());

        Vector2f closestPointToCircle = new Vector2f(localCirclePos);

        if (closestPointToCircle.x < min.x) {
            closestPointToCircle.x = min.x;
        } else if (closestPointToCircle.x > max.x) {
            closestPointToCircle.x = max.x;
        }

        if (closestPointToCircle.y < min.y) {
            closestPointToCircle.y = min.y;
        } else if (closestPointToCircle.y > max.y) {
            closestPointToCircle.y = max.y;
        }

        Vector2f circleToBox = new Vector2f(localCirclePos).sub(closestPointToCircle);
        return circleToBox.lengthSquared() <= circle.getRadius() * circle.getRadius();
    }

    // ============================================
    // AABB vs. Primitive Tests
    // ============================================
    public static boolean AABBandCircle(AABB box, Circle circle) {
        return circleAndAABB(circle, box);
    }

    public static boolean AABBandAABB(AABB b1, AABB b2) {
        Vector2f axisToTest[] = { new Vector2f(0, 1), new Vector2f(1, 0) };
        for(int i = 0; i < axisToTest.length; i++) {
            if(!overlapOnAxis(b1, b2, axisToTest[i])) {
                return false;
            }
        }

        return true;
    }

    public static boolean AABBAndBox2D(AABB b1, Box2D b2) {
        Vector2f axisToTest[] = {
                new Vector2f(0, 1), new Vector2f(1, 0),
                new Vector2f(0, 1), new Vector2f(1, 0)
        };

        PhysicsMath.rotate(axisToTest[2], b2.getRigidbody2D().getRotation(), new Vector2f());
        PhysicsMath.rotate(axisToTest[3], b2.getRigidbody2D().getRotation(), new Vector2f());

        for(int i = 0; i < axisToTest.length; i++) {
            if(!overlapOnAxis(b1, b2, axisToTest[i])) {
                return false;
            }
        }

        return true;
    }

    // ============================================
    // SAT Helpers
    // ============================================
    private static boolean overlapOnAxis(AABB b1, AABB b2, Vector2f axis) {
        Vector2f interval1 = getInterval(b1, axis);
        Vector2f interval2 = getInterval(b2, axis);
        return ((interval2.x <= interval1.y) && (interval1.x <= interval2.y));
    }

    private static boolean overlapOnAxis(AABB b1, Box2D b2, Vector2f axis) {
        Vector2f interval1 = getInterval(b1, axis);
        Vector2f interval2 = getInterval(b2, axis);
        return ((interval2.x <= interval1.y) && (interval1.x <= interval2.y));
    }

    private static boolean overlapOnAxis(Box2D b1, Box2D b2, Vector2f axis) {
        Vector2f interval1 = getInterval(b1, axis);
        Vector2f interval2 = getInterval(b2, axis);
        return ((interval2.x <= interval1.y) && (interval1.x <= interval2.y));
    }

    private static Vector2f getInterval(AABB rect, Vector2f axis) {
        Vector2f result = new Vector2f(0, 0);

        Vector2f min = rect.getMin();
        Vector2f max = rect.getMax();

        Vector2f vertices[] = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, min.y), new Vector2f(max.x, max.y)
        };


        result.x = axis.dot(vertices[0]);
        result.y = result.x;

        for(int i = 1; i < 4; i++) {
            float projection = axis.dot(vertices[i]);
            if(projection < result.x) {
                result.x = projection;
            }

            if(projection > result.y) {
                result.y = projection;
            }
        }

        return result;
    }

    private static Vector2f getInterval(Box2D rect, Vector2f axis) {
        Vector2f result = new Vector2f(0, 0);

        Vector2f vertices[] = rect.getVertices();


        result.x = axis.dot(vertices[0]);
        result.y = result.x;

        for(int i = 1; i < 4; i++) {
            float projection = axis.dot(vertices[i]);
            if(projection < result.x) {
                result.x = projection;
            }

            if(projection > result.y) {
                result.y = projection;
            }
        }

        return result;
    }
}
