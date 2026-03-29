package engine.renderer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {
    private static long window;

    public void init() {
        GLFWErrorCallback.createPrint(System.err);

        if (!GLFW.glfwInit()) {
            throw new IllegalArgumentException();
        }

        window = GLFW.glfwCreateWindow(1920, 1080, "TEMP", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException();
        }

        GLFW.glfwShowWindow(window);

        GL.createCapabilities();
        GLFW.glfwMakeContextCurrent(window);
    }

    public void loop() {
        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClearColor(1, 1, 1, 0);
        }
    }
}
