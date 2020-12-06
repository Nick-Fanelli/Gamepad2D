package gamepad.renderer;

import gamepad.object.GameObject;
import gamepad.object.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.Collections;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;
    private ArrayList<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if(spriteRenderer != null) add(spriteRenderer);
    }

    private void add(SpriteRenderer spriteRenderer) {
        boolean added = false;
        for(RenderBatch batch : batches) {
            if(batch.hasRoom() && batch.getzIndex() == spriteRenderer.gameObject.getzIndex()) {
                Texture texture = spriteRenderer.getTexture();
                if(texture == null || (batch.hasTexture(texture) || batch.hasTextureRoom())) {
                    batch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if(!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spriteRenderer.gameObject.getzIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spriteRenderer);
            Collections.sort(batches);
        }
    }

    public void render() {
        for(RenderBatch batch : batches) batch.render();
    }

}
