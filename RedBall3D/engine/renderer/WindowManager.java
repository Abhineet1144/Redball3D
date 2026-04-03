package engine.renderer;

import engine.core.PhysicsSystem;
import engine.entity.ECSWorld;
import engine.entity.components.CameraComponent;
import engine.scene.AbstractScene;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.util.Objects;

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
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key >= 0 && key < GLFW_KEY_LAST) {
                KEYS[key] = (action != GLFW_RELEASE);
            }
        });
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            MouseInput.onMouseMove(xpos, ypos);
        });
        glfwSetFramebufferSizeCallback(window, (w, width, height) -> {
            if (height == 0) return; // prevent divide by zero
            glViewport(0, 0, width, height);
            Objects.requireNonNull(ECSWorld.findGameObjectByName("Camera")).getComponent(CameraComponent.class).getCamera().adjustProjection(width, height);
        });

        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        PhysicsSystem.setup();

        this.currentScene = scene;
        this.currentScene.start();
    }

    public void loop(Shader shader) {

        shader.use();
        shader.setInt("diffuseMap", 0);
        shader.setInt("normalMap", 1);
        shader.setInt("specularMap", 2);
        shader.setInt("heightMap", 3);
        shader.setInt("roughnessMap", 4);
        shader.setFloat("heightScale", 0.1f);

        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            double time = glfwGetTime();
            float deltaTime = (float)(time - lastTime);
            lastTime = time;

            PhysicsSystem.update(deltaTime);

            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            GL11.glDisable(GL_DEPTH_TEST);
            PhysicsSystem.getDynamicsWorld().debugDrawWorld();
            GL11.glEnable(GL_DEPTH_TEST);
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