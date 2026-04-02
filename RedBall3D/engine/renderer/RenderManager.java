package engine.renderer;

import engine.core.Engine;
import engine.entity.ECSWorld;
import engine.entity.GameObject;
import engine.entity.components.CameraComponent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;


public class RenderManager {
    private static List<BatchRenderer> batches = new ArrayList<>();

    public static void prepare() {
        List<GameObject> gos = ECSWorld.getGameObjects();

        for (int i = 0; i < gos.size(); i += 100) {
            List<GameObject> chunk = gos.subList(i, Math.min(i + 100, gos.size()));
            BatchRenderer batch = new BatchRenderer(chunk);
            batch.prepare();
            batches.add(batch);
        }
    }

    public static void render(GameObject camera) {
        Engine.getShader().setMat4f("projection", camera.getComponent(CameraComponent.class).getProjectionMatrix());
        Engine.getShader().setMat4f("view", camera.getComponent(CameraComponent.class).getViewMatrix());
        // matrices
        Engine.getShader().setMat4f("view", camera.getComponent(CameraComponent.class).getViewMatrix());
        Engine.getShader().setMat4f("projection", camera.getComponent(CameraComponent.class).getProjectionMatrix());

        // transform
        Matrix4f trans = new Matrix4f();
        Engine.getShader().setMat4f("transform", trans);

        Engine.getShader().setVec3("lights[0].position", 0.0f, 5.0f, 2.0f);
        Engine.getShader().setVec3("lights[0].ambient",  0.3f, 0.1f, 0.0f);
        Engine.getShader().setVec3("lights[0].diffuse",  1.0f, 0.4f, 0.0f);
        Engine.getShader().setVec3("lights[0].specular", 1.0f, 0.5f, 0.0f);

        Engine.getShader().setVec3("lights[1].position", 0.0f, -5.0f, 2.0f);
        Engine.getShader().setVec3("lights[1].ambient",  0.0f, 0.0f, 0.2f);
        Engine.getShader().setVec3("lights[1].diffuse",  0.0f, 0.3f, 1.0f);
        Engine.getShader().setVec3("lights[1].specular", 0.0f, 0.4f, 1.0f);

        // CLEAR
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for (BatchRenderer batchRenderer : batches) {
            batchRenderer.render(Engine.getShader());
        }
    }

    public static void clear() {
        batches.clear();
    }
}