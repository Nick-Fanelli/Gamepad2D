package gamepad.renderer;

import org.lwjgl.opengl.GL30;

public class PickingTexture {

    private int pickingTextureID;
    private int fbo;
    private int depthTexture;

    public PickingTexture(int width, int height) {
        if(!init(width, height)) {
            assert false : "Error picking texture";
        }
    }

    public boolean init(int width, int height) {
        // Generate framebuffer
        fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

        // Create texture to render the data to, and attach it to framebuffer
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, pickingTextureID);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, this.texture.getID(), 0);

        // Create renderbuffer store depth data
        int rboID = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rboID);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32, width, height);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, rboID);

        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            assert false: "Error: Framebuffer is not complete!";
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

}
