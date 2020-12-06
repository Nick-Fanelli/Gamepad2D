package gamepad.object.components;

import gamepad.Window;
import gamepad.listener.MouseListener;
import gamepad.object.GameObject;
import gamepad.utils.Settings;
import org.lwjgl.glfw.GLFW;

public class MouseControls extends Component {

    public GameObject holdingObject = null;

    public void pickupObject(GameObject gameObject) {
        this.holdingObject = gameObject;
        Window.getScene().addGameObjectToScene(gameObject);
    }

    public void place() {
        this.holdingObject = null;
    }

    @Override
    public void update(float deltaTime) {
        if(holdingObject != null) {
            holdingObject.transform.position.x = MouseListener.getOrthoX() - 16;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - 16;
            holdingObject.transform.position.x = (int) (holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int) (holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            if(MouseListener.mouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                this.place();
            }
        }
    }
}
