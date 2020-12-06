package gamepad.renderer;

import gamepad.Window;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;

        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitSource = source.split("(#type)( )+([a-zA-Z])+");

            // Find first pattern after #type
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf(Window.get().isWindows ? "\r\n" : "\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find second pattern after #type
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf(Window.get().isWindows ? "\r\n" : "\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex")) {
                vertexSource = splitSource[1];
            } else if(firstPattern.equals("fragment")) {
                fragmentSource = splitSource[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if(secondPattern.equals("vertex")) {
                vertexSource = splitSource[2];
            } else if(secondPattern.equals("fragment")) {
                fragmentSource = splitSource[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }
    }

    public void compile() {
        // ==============================================================
        // Compile and link shaders
        // ==============================================================
        int vertexID, fragmentID;

        // Load and compile vertex shader
        vertexID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        // Pass the shader source into the GPU
        GL20.glShaderSource(vertexID, vertexSource);
        GL20.glCompileShader(vertexID);

        // Check for errors in compilation
        int success = GL20.glGetShaderi(vertexID, GL20.GL_COMPILE_STATUS);
        if(success == GL20.GL_FALSE) {
            int length = GL20.glGetShaderi(vertexID, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(GL20.glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }

        // Load and compile fragment shader
        fragmentID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        // Pass the shader source into the GPU
        GL20.glShaderSource(fragmentID, fragmentSource);
        GL20.glCompileShader(fragmentID);

        // Check for errors in compilation
        success = GL20.glGetShaderi(fragmentID, GL20.GL_COMPILE_STATUS);
        if(success == GL20.GL_FALSE) {
            int length = GL20.glGetShaderi(fragmentID, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(GL20.glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramID = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgramID, vertexID);
        GL20.glAttachShader(shaderProgramID, fragmentID);

        GL20.glLinkProgram(shaderProgramID);
        success = GL20.glGetProgrami(shaderProgramID, GL20.GL_LINK_STATUS);
        if(success == GL20.GL_FALSE) {
            int length = GL20.glGetProgrami(shaderProgramID, GL20.GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
            System.out.println(GL20.glGetProgramInfoLog(shaderProgramID, length));
            assert false : "";
        }
    }

    public void attach() {
        if(!beingUsed)
            GL30.glUseProgram(shaderProgramID);
        beingUsed = true;
    }

    public void detach() {
        GL30.glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = GL30.glGetUniformLocation(shaderProgramID, varName);
        this.attach();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        GL30.glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = GL30.glGetUniformLocation(shaderProgramID, varName);
        this.attach();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        GL30.glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4f) {
        int varLocation = GL30.glGetUniformLocation(shaderProgramID, varName);
        this.attach();
        GL30.glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    public void uploadFloat(String varName, float value) {
        int varLocation = GL30.glGetUniformLocation(shaderProgramID, varName);
        this.attach();
        GL30.glUniform1f(varLocation, value);
    }

    public void uploadInt(String varName, int value) {
        int varLocation = GL30.glGetUniformLocation(shaderProgramID, varName);
        this.attach();
        GL30.glUniform1i(varLocation, value);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = GL30.glGetUniformLocation(shaderProgramID, varName);
        this.attach();
        GL30.glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = GL30.glGetUniformLocation(shaderProgramID, varName);
        this.attach();
        GL30.glUniform1iv(varLocation, array);
    }
}
