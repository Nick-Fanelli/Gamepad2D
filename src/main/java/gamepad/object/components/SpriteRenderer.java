package gamepad.object.components;

import gamepad.renderer.Texture;
import gamepad.utils.Transform;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;
    private transient boolean shouldRedraw = true;


    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float deltaTime) {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            this.shouldRedraw = true;
        }
    }

    @Override
    public void imgui() {
        float[] imColor = { color.x, color.y, color.z, color.w };
        if(ImGui.colorPicker4("Color Picker: ", imColor)) {
            this.color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            this.shouldRedraw = true;
        }
    }

    public Vector4f getColor() { return color; }
    public Texture getTexture() {
        return sprite.getTexture();
    }
    public Vector2f[] getTexCoords() {
       return sprite.getTexCoords();
    }
    public boolean shouldRedraw() { return this.shouldRedraw; }

    public void setDrawn() { this.shouldRedraw = false; }

    // TODO: Update so that we check to see if sprite was actually updated.
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.shouldRedraw = true;
    }

    public void setColor(Vector4f color) {
        if(!this.color.equals(color)) {
            this.color.set(color);
            this.shouldRedraw = true;
        }
    }

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }

}
