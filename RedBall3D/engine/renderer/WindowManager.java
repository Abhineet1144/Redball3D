package engine.renderer;

import engine.scene.AbstractScene;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class WindowManager {
    private static long window;
    private static final boolean[] KEYS = new boolean[GLFW_KEY_LAST];
    private AbstractScene currentScene;

    public void init(AbstractScene scene) {
        GLFWErrorCallback.createPrint(System.err);

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(1920, 1080, "Engine", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        // key callbacks
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key >= 0 && key < GLFW_KEY_LAST) {
                KEYS[key] = (action != GLFW_RELEASE);
            }
        });

        this.currentScene = scene;
        this.currentScene.start();
    }

    public void loop() {
        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            double time = glfwGetTime();
            float deltaTime = (float)(time - lastTime);
            lastTime = time;

            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            currentScene.update(deltaTime);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static boolean isKeyDown(int keyCode) {
        return KEYS[keyCode];
    }

    public static long getWindow() {
        return window;
    }
}