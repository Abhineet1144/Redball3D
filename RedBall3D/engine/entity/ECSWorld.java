package engine.entity;

import engine.entity.components.*;

import java.util.ArrayList;
import java.util.List;

public class ECSWorld {
    private static List<GameObject> gameObjects = new ArrayList<>();
    private static final List<GameObject> pendingAdd = new ArrayList<>();

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

    public static void clearGameObjects() {
        gameObjects = new ArrayList<>();
    }

    public static boolean removeGameObject(String name) {
        GameObject go = findGameObjectByName(name);
        if (go == null) {
            assert false : "FAILED: TO REMOVE GAMEOBJECT, IS NULL";
            return false;
        }
        return removeGameObject(go);
    }

    public static void removeAll() {
        gameObjects = new ArrayList<>();
    }

    public static void start() {
        for (GameObject g : gameObjects) {
            g.start();
        }
    }

    public static List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public static void setGameObjects(List<GameObject> gameObjects) {
        ECSWorld.gameObjects = gameObjects;
    }
}