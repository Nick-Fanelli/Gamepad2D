package gamepad.listener;

import gamepad.Window;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static MouseListener instance;

    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;

    private boolean[] mouseButtonPressed = new boolean[9];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0;
        this.scrollY = 0;
        this.xPos = 0;
        this.yPos = 0;
        this.lastX = 0;
        this.lastY = 0;
    }

    public static MouseListener get() {
        if(MouseListener.instance == null) MouseListener.instance = new MouseListener();
        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = (get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2]);
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if(button < get().mouseButtonPressed.length)
                get().mouseButtonPressed[button] = true;
        } else if (action == GLFW_RELEASE) {
            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() { return (float) get().xPos; }
    public static float getY() { return (float) get().yPos; }
    public static float getDx() { return (float) (get().lastX = get().xPos); }
    public static float getDy() { return (float) (get().lastY = get().yPos); }
    public static float getScrollX() { return (float) get().scrollX; }
    public static float getScrollY() { return (float) get().scrollY; }

    public static float getOrthoX() {
        float currentX = getX();
        currentX = (currentX / (float) Window.getWidth()) * 2f - 1f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
        tmp.mul(Window.getScene().getCamera().getInverseProjection()).mul(Window.getScene().getCamera().getInverseView());
        currentX = tmp.x;

        return currentX;
    }
    public static float getOrthoY() {
        float currentY = Window.getHeight() - getY();
        currentY = (currentY / (float) Window.getHeight()) * 2f - 1f;
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);
        tmp.mul(Window.getScene().getCamera().getInverseProjection()).mul(Window.getScene().getCamera().getInverseView());
        currentY = tmp.y;

        return currentY;
    }

    public static boolean isDragging() { return get().isDragging; }

    public static boolean mouseButtonDown(int button) {
        if(button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}
