package gamepad.scenes;

import gamepad.Camera;
import gamepad.Prefabs;
import gamepad.object.GameObject;
import gamepad.object.components.MouseControls;
import gamepad.object.components.Sprite;
import gamepad.object.components.Spritesheet;
import gamepad.physics2D.PhysicsSystem2D;
import gamepad.physics2D.rigidbody.Rigidbody2D;
import gamepad.renderer.DebugDraw;
import gamepad.utils.AssetPool;
import gamepad.utils.Transform;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;
    public GameObject levelEditorComponents = new GameObject("Level Editor", new Transform(new Vector2f()), 0);
    public PhysicsSystem2D physics = new PhysicsSystem2D(1.0f / 60.0f, new Vector2f(0, -10));
    public Transform obj1, obj2;
    public Rigidbody2D rb1, rb2;

    @Override
    public void init() {
        levelEditorComponents.addComponent(new MouseControls());
//        levelEditorComponents.addComponent(new GridLines());

        obj1 = new Transform(new Vector2f(100, 500));
        obj2 = new Transform(new Vector2f(200, 500));
        rb1 = new Rigidbody2D();
        rb2 = new Rigidbody2D();
        rb1.setRawTransform(obj1);
        rb2.setRawTransform(obj2);
        rb1.setMass(100.0f);
        rb2.setMass(200.0f);

        physics.addRigidbody(rb1);
        physics.addRigidbody(rb2);

        loadResources();

        this.camera = new Camera(new Vector2f(0, 0));

        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        if(levelLoaded) {
//            this.activeGameObject = gameObjects.get(0);
            return;
        }
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16,
                        81, 0));
    }


    @Override
    public void update(float deltaTime) {
        levelEditorComponents.update(deltaTime);

        for(GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

        DebugDraw.addBox2D(obj1.position, new Vector2f(32, 32), 0.0f, new Vector3f(1, 0, 0));
        DebugDraw.addBox2D(obj2.position, new Vector2f(32, 32), 0.0f, new Vector3f(0.2f, 0.8f, 0.1f));
        physics.update(deltaTime);

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
