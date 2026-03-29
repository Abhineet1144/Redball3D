//package engine.scene;
//
//import engine.entity.ECSWorld;
//import engine.renderer.RenderManager3D;
//
///**
// * Manages the active scene lifecycle: load, init, update, cleanup.
// *
// * Usage:
// *   SceneManager.loadScene(new GameScene());
// *   // every frame:
// *   SceneManager.update(dt);
// */
//public class SceneManager {
//
//    private static AbstractScene activeScene  = null;
//    private static AbstractScene pendingScene = null;
//
//    private SceneManager() {}
//
//    /** Queue a scene for loading at the start of the next frame. */
//    public static void loadScene(AbstractScene scene) {
//        pendingScene = scene;
//    }
//
//    /**
//     * Must be called once per frame from the main game loop.
//     * Handles deferred transitions, first-time init (including GPU upload),
//     * and per-frame update.
//     */
//    public static void update(float dt) {
//        // ── deferred transition ──────────────────────────────────────────────
//        if (pendingScene != null) {
//            if (activeScene != null) {
//                activeScene.cleanup();
//                ECSWorld.clearGameObjects();
//                RenderManager3D.clear();
//            }
//            activeScene  = pendingScene;
//            pendingScene = null;
//        }
//
//        if (activeScene == null) return;
//
//        // ── first-time init + GPU upload ─────────────────────────────────────
//        if (!activeScene.isInitialized()) {
//            activeScene.init();           // scene populates ECSWorld
//            RenderManager3D.prepare();    // upload all MeshRenderers to GPU
//            activeScene.markInitialized();
//        }
//
//        // ── per-frame update ─────────────────────────────────────────────────
//        activeScene.update(dt);
//    }
//
//    public static AbstractScene getActiveScene() { return activeScene; }
//}
