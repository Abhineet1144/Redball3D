package engine.scene;

import engine.core.Engine;
import engine.entity.ECSWorld;
import engine.entity.GameObject;
import engine.entity.components.CameraComponent;
import engine.entity.components.MeshRenderer;
import engine.entity.components.MeshRenderer.Vertex;
import engine.entity.components.Transform;
import engine.renderer.*;
import engine.renderer.texture.Texture;
import engine.utils.AssetPool;

import imgui.extension.imguizmo.flag.Mode;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL33.*;

public class GameScene extends AbstractScene {

    private GameObject camera;
    private GameObject obj;
    private GameObject obj2;

    @Override
    public void start() {
        // camera
        camera = ECSWorld.createGameObject("Camera");
        camera.addComponent(new Transform(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(), new Vector3f()));
        camera.addComponent(new CameraComponent(1920, 1080));

        // load model
        ModelLoader.ModelData data = ModelLoader.loadModel("res/backpack/backpack.obj");
        ModelLoader.ModelData sofaData = ModelLoader.loadModel("res/sofa/source/ready.obj");

        obj = ECSWorld.createGameObject("Mesh_BackPack");
        obj.addComponent(new Transform(new Vector3f(0.0f, -3.0f, 0.0f), new Vector3f(), new Vector3f(1.0f)));
        obj.addComponent(new MeshRenderer(data));

        obj2 = ECSWorld.createGameObject("sofa");
        obj2.addComponent(new Transform(new Vector3f(), new Vector3f(), new Vector3f(1,1,1)));
        obj2.addComponent(new MeshRenderer(sofaData));

        RenderManager.prepare();
    }

    @Override
    public void update(float dt) {
        // camera movement
        float speed = 20.0f;
        Transform t = camera.getComponent(Transform.class);

        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_RIGHT))
            t.setXPosition(t.getXPosition() - speed * dt);
        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_LEFT))
            t.setXPosition(t.getXPosition() + speed * dt);
        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_UP))
            t.setZPosition(t.getZPosition() + speed * dt);
        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_DOWN))
            t.setZPosition(t.getZPosition() - speed * dt);

        ECSWorld.update(dt);

        RenderManager.render(camera);
    }
}