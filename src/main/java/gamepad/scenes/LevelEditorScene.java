package gamepad.scenes;

import gamepad.Camera;
import gamepad.Prefabs;
import gamepad.object.GameObject;
import gamepad.object.components.*;
import gamepad.renderer.DebugDraw;
import gamepad.utils.AssetPool;
import gamepad.utils.Transform;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;
//    private GameObject obj1;

    public GameObject levelEditorComponents = new GameObject("Level Editor", new Transform(new Vector2f()), 0);

    @Override
    public void init() {
        levelEditorComponents.addComponent(new MouseControls());
        levelEditorComponents.addComponent(new GridLines());

        loadResources();

        this.camera = new Camera(new Vector2f(0, 0));

        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        if(levelLoaded) {
//            this.activeGameObject = gameObjects.get(0);
            return;
        }
//
//        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
//        SpriteRenderer obj1SpriteRenderer = new SpriteRenderer();
//        obj1SpriteRenderer.setColor(new Vector4f(1, 0, 0, 1));
//        obj1.addComponent(obj1SpriteRenderer);
//        obj1.addComponent(new Rigidbody());
//        this.addGameObjectToScene(obj1);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16,
                        81, 0));
    }

    float x = 0;
    float y = 0;

    @Override
    public void update(float deltaTime) {
        levelEditorComponents.update(deltaTime);

        DebugDraw.addCircle(new Vector2f(x, y), 64, new Vector3f(0, 0, 0), 1);
        x += 50f * deltaTime;
        y += 50f * deltaTime;

        for(GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for(int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if(ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                levelEditorComponents.getComponent(MouseControls.class).pickupObject(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPosition = new ImVec2();
            ImGui.getItemRectMax(lastButtonPosition);
            float lastButtonX2 = lastButtonPosition.x;
            float nexButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if(i + 1 < sprites.size() && nexButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }

        ImGui.end();

    }
}
