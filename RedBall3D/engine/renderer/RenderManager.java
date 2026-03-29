//package engine.renderer;
//
//import engine.core.Engine;
//import engine.entity.ECSWorld;
//import engine.entity.GameObject;
//import redball.engine.entity.components.CameraComponent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
//import static org.lwjgl.opengl.GL30.*;
//import static engine.renderer.BatchRenderer.MAX_ENTITIES;
//
//
//public class RenderManager {
//    private static List<BatchRenderer> batches = new ArrayList<>();
//
//    public static void prepare(GameObject camera) {
//        List<GameObject> gos = ECSWorld.getGameObjects();
//
//        for (int i = 0; i < gos.size(); i += MAX_ENTITIES) {
//            List<GameObject> chunk = gos.subList(i, Math.min(i + MAX_ENTITIES, gos.size()));
//            BatchRenderer batch = new BatchRenderer(chunk);
//            batch.prepare();
//            batches.add(batch);
//        }
//        camera.start();
//    }
//
//    public static void rebuild() {
//        batches.clear();
//        List<GameObject> gos = ECSWorld.getGameObjects();
//
//        for (int i = 0; i < gos.size(); i += MAX_ENTITIES) {
//            List<GameObject> chunk = gos.subList(i, Math.min(i + MAX_ENTITIES, gos.size()));
//            BatchRenderer batch = new BatchRenderer(chunk);
//            batch.prepare();
//            batches.add(batch);
//        }
//    }
//
//    public static void render(GameObject camera) {
//        Engine.getShader().setMat4f("projection", camera.getComponent(CameraComponent.class).getProjectionMatrix());
//        Engine.getShader().setMat4f("view", camera.getComponent(CameraComponent.class).getViewMatrix());
//
//        Engine.getShader().initTextureSamplers();
//
//        // CLEAR
//        glClearColor(0, 0, 0, 1);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//        for (BatchRenderer batchRenderer : batches) {
//            batchRenderer.bindTextures();
//            batchRenderer.updateVertices();
//            batchRenderer.render();
//        }
//        // Blit FBO to window
//        glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer.getFboId());
//        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
//        glBlitFramebuffer(
//                0, 0, frameBuffer.getWidth(), frameBuffer.getHeight(),
//                0, 0, frameBuffer.getWidth(), frameBuffer.getHeight(),
//                GL_COLOR_BUFFER_BIT, GL_NEAREST
//        );
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//
//    }
//
//    public static FrameBuffer getFrameBuffer() {
//        return frameBuffer;
//    }
//
//    public static void clear() {
//        batches.clear();
//    }
//}