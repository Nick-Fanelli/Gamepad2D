package gamepad;

import gamepad.listener.KeyListener;
import gamepad.listener.MouseListener;
import gamepad.renderer.DebugDraw;
import gamepad.renderer.FrameBuffer;
import gamepad.scenes.LevelEditorScene;
import gamepad.scenes.LevelScene;
import gamepad.scenes.Scene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public final boolean isWindows;

    private static Window window = null;

    private int width, height;
    private String title;

    private static long glfwWindow;
    private ImGUILayer imGUILayer;
    private FrameBuffer frameBuffer;

    private static Scene currentScene = null;

//    public float r = 38f/255f, g = 77f/255f, b = 142f/255f;
    private final float r = 1, g = 1, b = 1;

    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "Gamepad Engine";

        String os = System.getProperty("os.name");
        isWindows = os.trim().toLowerCase().startsWith("windows");
    }

    public static Window get() {
        if(Window.window == null) Window.window = new Window();

        return Window.window;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown Scene '" + newScene + "'";
                break;
        }

        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Scene getScene() {
        return get().currentScene;
    }

    public void run() {
        System.out.println("Initializing Gamepad Engine with LWJGL " + Version.getVersion() + "!");

        this.init();
        this.loop();

        // Free Memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public void init() {
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        // Configure GLFW
        glfwDefaultWindowHints();

        // MacOS Compatibility
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
//        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if(glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create GLFW window!");
        }

        // Mouse Listener Callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        try(MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(glfwWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidmode != null;
            glfwSetWindowPos(
                    glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Blend Alpha
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        this.imGUILayer = new ImGUILayer(glfwWindow);
        this.imGUILayer.initImGui();

        this.frameBuffer = new FrameBuffer(1920, 1080);
        glViewport(0, 0, 1920, 1080);

        Window.changeScene(0);
    }

    public void loop() {

        float endTime, beginTime = (float) glfwGetTime();
        float deltaTime = -1f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents(); // Handle Events

            DebugDraw.beginFrame();

            this.frameBuffer.bind();

            // Clear Screen
            glClearColor(r, g, b, 1f);
            glClear(GL_COLOR_BUFFER_BIT);

            if(deltaTime >= 0) {
                DebugDraw.draw();
                currentScene.update(deltaTime);
            }
            this.frameBuffer.unBind();

            this.imGUILayer.update(deltaTime, currentScene);

            glfwSwapBuffers(glfwWindow);

            endTime = (float) glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }
        currentScene.saveExit();
    }

    public static int getWidth() { return get().width; }
    public static int getHeight() { return get().height; }

    public static void setWidth(int newWidth) { get().width = newWidth; }
    public static void setHeight(int newHeight) { get().height = newHeight; }

    public static FrameBuffer getFrameBuffer() { return get().frameBuffer; }
    public static float getTargetAspectRatio() { return 16.0f / 9.0f; }
}
