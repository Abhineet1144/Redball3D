import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.entity.GameObject;
import engine.entity.components.CameraComponent;
import engine.entity.components.MeshRenderer;
import engine.entity.components.Transform;
import engine.renderer.BatchRenderer;
import engine.renderer.ModelLoader;
import engine.renderer.Shader;
import engine.renderer.Texture;
import engine.utils.AssetPool;
import engine.entity.components.MeshRenderer.Vertex;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    // 1. SHADERS: Note the 'layout' locations and the 'out/in' variables
    private final String vertexShaderSource = AssetPool.getVertexShaderSource();
    private final String fragmentShaderSource = AssetPool.getFragmentShaderSource();
    private static final boolean[] KEYS = new boolean[GLFW_KEY_LAST];

    public void run() {
        initKeyCallbacks();
        loop();
        // Cleanup
        glfwTerminate();
    }

    private long window;

    private void initKeyCallbacks() {
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        window = glfwCreateWindow(1920, 1080, "Rainbow Triangle", NULL, NULL);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        initKeyCallbacks(window);
    }

    private void loop() {
        GameObject camera = new GameObject("Camera");
        camera.addComponent(new Transform(new Vector3f(0.0f, 0.0f, -20.0f), new Vector3f(), new Vector3f()));
        camera.addComponent(new CameraComponent(1920, 1080));

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        Shader shader = new Shader(vertexShaderSource, fragmentShaderSource);

        List<ModelLoader.Mesh> meshes = ModelLoader.loadModel("res/backpack/backpack.obj");
        Vertex[] vertices = new Vertex[49041];
        int[] indices = new int[203721];
        Texture texture = null;
        GameObject obj = new GameObject("Mesh_BackPack");
        Map<String, Texture> textureCache = new HashMap<>();

        // Variables to store the loaded textures
        Texture diffuseTexture = null;
        Texture normalTexture = null;
        Texture specularTexture = null;
        int vCount = 0;
        int iCount = 0;

        for (int i = 0; i < meshes.size(); i++) {
            ModelLoader.Mesh mesh = meshes.get(i);
            int vertexOffset = vCount;
            // Load diffuse texture
            String diffusePath = mesh.material.diffuseMap;
            if (diffusePath != null && !textureCache.containsKey(diffusePath)) {
                textureCache.put(diffusePath, new Texture(diffusePath));
                diffuseTexture = textureCache.get(diffusePath);
                System.out.println("Loaded unique diffuse texture: " + diffusePath);
            }

            // Load normal texture
            String normalPath = mesh.material.normalMap;
            if (normalPath != null && !textureCache.containsKey(normalPath)) {
                textureCache.put(normalPath, new Texture(normalPath));
                normalTexture = textureCache.get(normalPath);
                System.out.println("Loaded unique normal texture: " + normalPath);
            }

            // Load specular texture
            String specularPath = mesh.material.specularMap;
            if (specularPath != null && !textureCache.containsKey(specularPath)) {
                textureCache.put(specularPath, new Texture(specularPath));
                specularTexture = textureCache.get(specularPath);
                System.out.println("Loaded unique specular texture: " + specularPath);
            }

            texture = textureCache.getOrDefault(diffusePath, new Texture("res/container.jpg"));

            for (Vertex vertex : mesh.vertices) {
                vertices[vCount++] = vertex;
            }

            for (int index : mesh.indices) {
                indices[iCount++] = index + vertexOffset;
            }

        }
        obj.addComponent(new MeshRenderer(
                vertices,
                indices,
                texture
        ));
        // Fallback textures if not loaded
        if (diffuseTexture == null) diffuseTexture = new Texture("res/container.jpg");
        if (normalTexture == null) normalTexture = diffuseTexture;  // Use diffuse as fallback
        if (specularTexture == null) specularTexture = diffuseTexture;  // Use diffuse as fallback

        List<MeshRenderer> renderers = new ArrayList<>();

        renderers.add(obj.getComponent(MeshRenderer.class));

        BatchRenderer batch = new BatchRenderer(renderers);
        int vao = batch.renderAll();
        glfwSwapInterval(0);

        double lastTime = 0;
        int frames = 0;

        while (!glfwWindowShouldClose(window)) {
            double time = glfwGetTime();
            double deltaTime = time - lastTime;
            lastTime = time;

            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            shader.use();

            // Bind all textures
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, diffuseTexture.getTexID());
            shader.setInt("diffuseMap", 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, normalTexture.getTexID());
            shader.setInt("normalMap", 1);

            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, specularTexture.getTexID());
            shader.setInt("specularMap", 2);

            // Set matrices
            shader.setMat4f("view", camera.getComponent(CameraComponent.class).getViewMatrix());
            shader.setMat4f("projection", camera.getComponent(CameraComponent.class).getProjectionMatrix());

            // Transform
            Matrix4f trans = new Matrix4f();
            trans.translate(0.0f, 0.0f, 5.0f);
            trans.rotate((float) glfwGetTime(), 0.0f, 1.0f, 0.0f);
            trans.scale(1.0f);
            shader.setMat4f("transform", trans);

            // Example: set up two lights
            // Light 1 - Top (warm orange)
            shader.setVec3("lights[0].position", 0.0f, 5.0f, 2.0f);
            shader.setVec3("lights[0].ambient",  0.3f, 0.1f, 0.0f);
            shader.setVec3("lights[0].diffuse",  1.0f, 0.4f, 0.0f);
            shader.setVec3("lights[0].specular", 1.0f, 0.5f, 0.0f);

            // Light 2 - Bottom (cool blue)
            shader.setVec3("lights[1].position", 0.0f, -5.0f, 2.0f);
            shader.setVec3("lights[1].ambient",  0.0f, 0.0f, 0.2f);
            shader.setVec3("lights[1].diffuse",  0.0f, 0.3f, 1.0f);
            shader.setVec3("lights[1].specular", 0.0f, 0.4f, 1.0f);

            glBindVertexArray(vao);

            float speed = 20.0f;

            if (isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
                Transform t = camera.getComponent(Transform.class);
                t.setXPosition((float) (t.getXPosition() - speed * deltaTime));
            }

            if (isKeyDown(GLFW.GLFW_KEY_LEFT)) {
                Transform t = camera.getComponent(Transform.class);
                t.setXPosition((float) (t.getXPosition() + speed * deltaTime));
            }

            if (isKeyDown(GLFW.GLFW_KEY_DOWN)) {
                Transform t = camera.getComponent(Transform.class);
                t.setZPosition((float) (t.getZPosition() - speed * deltaTime));
            }

            if (isKeyDown(GLFW_KEY_UP)) {
                Transform t = camera.getComponent(Transform.class);
                t.setZPosition((float) (t.getZPosition() + speed * deltaTime));
            }

            camera.update((float) deltaTime);

            int totalIndices = 0;
            totalIndices += obj.getComponent(MeshRenderer.class).eboVal.length;

            glDrawElements(GL_TRIANGLES, totalIndices, GL_UNSIGNED_INT, 0L);
            glfwSwapBuffers(window);
            glfwPollEvents();

            frames++;
            if ((time - lastTime) > 1.0) {
                glfwSetWindowTitle(window, "FPS:" + frames);
                frames = 0;
                lastTime = time;
            }
        }
    }

    public static void initKeyCallbacks(long window) {
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            if (key >= 0 && key < GLFW_KEY_LAST) {
                KEYS[key] = (action != GLFW_RELEASE);
            }
        });
    }

    public static boolean isKeyDown(int keyCode) {
        return KEYS[keyCode];
    }

    public static void main(String[] args) {
        new Main().run();
    }
}