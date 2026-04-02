package engine.core;

import engine.entity.ECSWorld;
import engine.entity.GameObject;
import engine.renderer.Shader;
import engine.renderer.WindowManager;
import engine.scene.AbstractScene;
import engine.utils.AssetPool;

public class Engine {
    private static boolean started = false;
    private static WindowManager windowManager = null;
    private static Shader shader = null;

    public static void start(AbstractScene scene) throws Exception {
        if (started) return;

        started = true;
        windowManager = new WindowManager();
        windowManager.init(scene);

        shader = new Shader(AssetPool.getVertexShader(), AssetPool.getFragmentShader());
        windowManager.loop(shader);
    }

    public static WindowManager getWindowManager() {
        return windowManager;
    }

    public static Shader getShader() {
        return shader;
    }
}