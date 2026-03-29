package engine.scene;

/**
 * Base class for all game scenes.
 * Override init() to populate the scene with GameObjects,
 * update() for per-frame logic, and cleanup() for teardown.
 */
public abstract class AbstractScene {

    protected boolean initialized = false;

    /**
     * Called once when the scene is first loaded.
     * Spawn GameObjects, load assets, configure the camera here.
     */
    public abstract void init();

    /**
     * Called every frame after physics and input processing.
     *
     * @param dt delta time in seconds
     */
    public abstract void update(float dt);

    /**
     * Called when the scene is unloaded (before a new scene is loaded).
     * Free GPU resources, clear ECSWorld, etc.
     */
    public abstract void cleanup();

    /**
     * Returns a human-readable name for this scene (used in window title / debug).
     */
    public abstract String getName();

    public boolean isInitialized() {
        return initialized;
    }

    protected void markInitialized() {
        initialized = true;
    }
}
