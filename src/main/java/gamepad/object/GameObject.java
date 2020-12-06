package gamepad.object;

import gamepad.object.components.Component;
import gamepad.utils.Transform;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int ID_COUNTER = 0;
    private int uid = -1;

    private String name;
    private ArrayList<Component> components;

    public Transform transform;
    private int zIndex;

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
        this.zIndex = zIndex;

        this.uid = ID_COUNTER++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for(Component c : components) {
            if(componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch(ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casing component.";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for(int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if(componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component component) {
        component.generateID();
        this.components.add(component);
        component.gameObject = this;
    }

    public void update(float deltaTime) {
        for (Component component : components) {
            component.update(deltaTime);
        }
    }

    public void imgui() {
        for(Component component : components) {
            component.imgui();
        }
    }

    public void start() {
        for(Component component : components) component.start();
    }

    public int getzIndex() { return this.zIndex; }
    public int getUid() { return this.uid; }
    public String getName() { return this.name; }
    public ArrayList<Component> getComponents() { return components; }

    public static void init(int maxID) {
        ID_COUNTER = maxID;
    }

    public List<Component> getAllComponents() { return this.components; }
}
