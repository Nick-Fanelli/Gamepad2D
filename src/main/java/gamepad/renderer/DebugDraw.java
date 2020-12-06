package gamepad.renderer;

import gamepad.Window;
import gamepad.utils.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Arrays;

public class DebugDraw {

    private static int MAX_LINES = 500;

    private static ArrayList<Line2D> lines = new ArrayList<>();
    // 6 floats per vertex, 2 verticies per line
    private static float[] vertexArray = new float[MAX_LINES * 6 * 2];

    private static Shader shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

    private static int vaoID;
    private static int vboID;

    private static boolean started = false;

    public static void start() {
        // Generate VAO
        vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        // Create the vbo and buffer some memory
        vboID = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL30.GL_DYNAMIC_DRAW);

        // Enable the vertex array attributes
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 6 * Float.BYTES, 0);
        GL30.glEnableVertexAttribArray(0);

        GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        GL30.glEnableVertexAttribArray(1);

        GL30.glLineWidth(2.0f);
    }

    public static void beginFrame() {
        if(!started) {
            start();
            started = true;
        }

        // Remove dead lines
        for(int i = 0; i < lines.size(); i++) {
            if(lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if(lines.size() <= 0) return;

        int index = 0;
        for(Line2D line : lines) {
            for(int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getStart() : line.getEnd();
                Vector3f color = line.getColor();

                // Load Position Into Float Array
                vertexArray[index] = position.x;
                vertexArray[index + 1] = position.y;
                vertexArray[index + 2] = -10.0f;

                // Load Color Into Float Array
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
        GL30.glBufferSubData(GL30.GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        // Attach Shader
        shader.attach();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        // Bind VAO
        GL30.glBindVertexArray(vaoID);
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);

        // Draw the batch
        GL30.glDrawArrays(GL30.GL_LINES, 0, lines.size() * 6 * 2);

        // Disable Location
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        // Detach Shader
        shader.detach();
    }

    // =======================================================
    // Add Line2D Methods
    // =======================================================
    public static void addLine2D(Vector2f start, Vector2f end) {
        // TODO: ADD CONSTANTS FOR COMMON COLORS
        addLine2D(start, end, new Vector3f(0, 1, 0), 1);
    }

    public static void addLine2D(Vector2f start, Vector2f end, Vector3f color) {
        addLine2D(start, end, color, 1);
    }

    public static void addLine2D(Vector2f start, Vector2f end, Vector3f color, int lifeTime) {
        if(lines.size() >= MAX_LINES) return;
        DebugDraw.lines.add(new Line2D(start, end, color, lifeTime));
    }

    // =======================================================
    // Add Box2D Methods
    // =======================================================
    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int lifeCycle) {
        Vector2f min = new Vector2f((center).sub(new Vector2f(dimensions).div(2f)));
        Vector2f max = new Vector2f((center).add(new Vector2f(dimensions).div(2f)));

        Vector2f[] vertices = {
            new Vector2f(min.x, min.y),
            new Vector2f(min.x, max.y),
            new Vector2f(max.x, max.y),
            new Vector2f(max.x, min.y),
        };

        if(rotation != 0.0f) {
            for(Vector2f vertex : vertices) {

            }
        }
    }

    // =======================================================
    // Add Circle Methods
    // =======================================================

}
