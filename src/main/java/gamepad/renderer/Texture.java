package gamepad.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {

    private String filepath;
    private transient int textureID;
    private int width, height;

    public Texture() {
        textureID = -1;
        width = -1;
        height = -1;
    }

    public Texture(int width, int height) {
        this.filepath = "#GENERATED#";

        // Generate Texture on GPU
        this.textureID = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);

        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGB, width, height,
                0, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE, 0);
    }

    public void init(String filepath) {
        this.filepath = filepath;

        // Generate Texture on GPU
        this.textureID = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);

        // Set the texture parameters
        // Repeat image in both directions
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
        // When stretching the image, pixelate.
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        // When shrinking an image, pixelate.
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        STBImage.stbi_set_flip_vertically_on_load(true); // Flip the image to correct vertical flip

        ByteBuffer image = STBImage.stbi_load(filepath, width, height, channels, 0);

        if(image != null) {
            this.width = width.get(0);
            this.height = height.get(0);

            if(channels.get(0) == 3) {
                GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGB, width.get(0), height.get(0),
                        0, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE, image);
            } else if(channels.get(0) == 4){
                GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, width.get(0), height.get(0),
                        0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error (Texture): Unknown number of channels '" + channels.get(0) + "'";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }

        STBImage.stbi_image_free(image);
    }

    public void bind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, textureID);
    }

    public void unbind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }

    public int getTextureID() { return textureID; }
    public String getFilepath() { return filepath; }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public int getID() { return this.textureID; }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Texture)) return false;
        Texture oTex = (Texture) obj;
        return oTex.getWidth() == this.width && oTex.getHeight() == this.height && oTex.getID() == this.textureID &&
                oTex.getFilepath().equals(this.filepath);
    }
}
