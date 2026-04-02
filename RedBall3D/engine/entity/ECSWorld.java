package engine.entity;

import engine.entity.components.*;

import java.util.ArrayList;
import java.util.List;

public class ECSWorld {
    private static List<GameObject> gameObjects = new ArrayList<>();

    public ECSWorld() {}

    public static GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        gameObjects.add(go);
        return go;
    }

    public static GameObject findGameObjectByName(String name) {
        for (GameObject g : gameObjects) {
            if (g.getName().equals(name)) return g;
        }
        return null;
    }

    public static boolean removeGameObject(GameObject gameObject) {
        for (GameObject g : gameObjects) {
            if (g.equals(gameObject)) {
                gameObjects.remove(g);
                assert true : "SUCCESS: REMOVED GAMEOBJECT";
                return true;
            }
        }
        assert false : "FAILED: TO REMOVE GAMEOBJECT";
        return false;
    }

    public static void start() {
        for (GameObject g : gameObjects) {
            g.start();
        }
    }

    public static void update(float delaTime) {
        for (GameObject g : gameObjects) {
            g.update(delaTime);
        }
    }

    public static List<GameObject> getGameObjects() {
        return gameObjects;
    }
}