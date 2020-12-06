package gamepad.scenes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gamepad.Camera;
import gamepad.data.typeAdapter.ComponentAdapter;
import gamepad.data.typeAdapter.GameObjectAdapter;
import gamepad.object.GameObject;
import gamepad.object.components.Component;
import gamepad.renderer.Renderer;
import imgui.ImGui;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;

    private boolean isRunning = false;
    protected ArrayList<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    protected boolean levelLoaded = false;

    public void init() {

    }

    public void start() {
        for(GameObject gameObject : gameObjects) {
            gameObject.start();
            this.renderer.add(gameObject);
        }
        this.isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject) {
        if(!isRunning) {
            gameObjects.add(gameObject);
        } else {
            gameObjects.add(gameObject);
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    public abstract void update(float deltaTime);

    public Camera getCamera() { return this.camera; }

    public void sceneImgui() {
        if(activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();
            ImGui.end();
        }

        imgui();
    }

    public void imgui() {

    }

    public void saveExit() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectAdapter())
                .create();

        try {
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentAdapter())
                .registerTypeAdapter(GameObject.class, new GameObjectAdapter())
                .create();
        String inFile = "";

        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!inFile.isBlank()) {
            int maxGoId = -1;
            int maxCompId = -1;

            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for(GameObject object : objs) {
                this.addGameObjectToScene(object);

                for(Component c : object.getAllComponents()) {
                    if(c.getUid() > maxCompId) {
                        maxCompId = c.getUid();
                    }
                }

                if(object.getUid() > maxGoId) maxGoId = object.getUid();
            }

            maxGoId++;
            maxCompId++;

            GameObject.init(maxGoId);
            Component.init(maxCompId);
            this.levelLoaded = true;
        }
    }

}
