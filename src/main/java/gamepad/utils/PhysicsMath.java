package gamepad.utils;

import org.joml.Vector2f;

/**
 * The type PhysicsMath is used to calculate physics based math problems.
 *
 * @author Nick Fanelli
 * @since 6-Dec-2020
 * @version 1.0
 */
public class PhysicsMath {

    /**
     * Will modify the Vector2f passed in!
     *
     * @param vec      the vector to be modified
     * @param angleDeg the angle in degrees
     * @param origin   the origin as a Vector2f
     */
    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }

    /**
     * Compare to a certain amount of precision
     *
     * @param x       the x variable to be compared as a float
     * @param y       the y variable to be compared as a float
     * @param epsilon the amount of precision required
     * @return true or false if it matches
     */
    public static boolean compare(float x, float y, float epsilon) {
        return Math.abs(x - y) <= epsilon * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    /**
     * Compare the vectors to a certain amount of precision
     *
     * @param vec1    the vec 1 to be compared
     * @param vec2    the vec 2 to be compared
     * @param epsilon the epsilon the amount of precision required
     * @return the boolean true or false if it matches
     */
    public static boolean compare(Vector2f vec1, Vector2f vec2, float epsilon) {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon);
    }

    /**
     * Compare to the Float.MIN_VALUE amount of precision.
     *
     * @param x the x variable to be compared as a float
     * @param y the y variable to be compared as a float
     * @return the boolean true or false if it matches
     */
    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    /**
     * Compare the vectors to the Float.MIN_VALUE amount of precision.
     *
     * @param vec1 the vec 1 to be compared
     * @param vec2 the vec 2 to be compared
     * @return the boolean true or false if it matches
     */
    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }
}
