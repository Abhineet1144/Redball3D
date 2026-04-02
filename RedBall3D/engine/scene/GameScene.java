package engine.scene;

import engine.entity.ECSWorld;
import engine.entity.GameObject;
import engine.entity.components.*;
import engine.renderer.*;

import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class GameScene extends AbstractScene {

    private GameObject camera;
    private GameObject obj;
    private GameObject obj2;

    @Override
    public void start() {
        // camera
        camera = ECSWorld.createGameObject("Camera");
        camera.addComponent(new Transform(new Vector3f(0.0f, 0.0f, 20.0f), new Vector3f(), new Vector3f()));
        camera.addComponent(new CameraComponent(1920, 1080));

        obj = ECSWorld.createGameObject("Mesh_BackPack");
        obj.addComponent(new Transform(new Vector3f(0.0f, -3.0f, 0.0f), new Vector3f(), new Vector3f(2.0f)));
        obj.addComponent(new MeshRenderer(ModelLoader.loadModel("res/Leon/leon.obj")));

        obj2 = ECSWorld.createGameObject("sofa");
        obj2.addComponent(new Transform(new Vector3f(), new Vector3f(0,Math.toRadians(90),0), new Vector3f(1.0f)));
        obj2.addComponent(new MeshRenderer(ModelLoader.loadModel("res/backpack/backpack.obj")));

        RenderManager.prepare();
    }

    @Override
    public void update(float dt) {
        // camera movement
        float speed = 15.0f * dt;
        Transform t = camera.getComponent(Transform.class);
        CameraComponent c = camera.getComponent(CameraComponent.class);
        Vector3f dir = new Vector3f(c.getCamera().front);
        Vector3f up  = new Vector3f(c.getCamera().cameraUp);

        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_D)) {
            Vector3f right = new Vector3f(up).cross(dir).normalize();
            t.position.sub(right.mul(speed));
        }
        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_A)) {
            Vector3f right = new Vector3f(up).cross(dir).normalize();
            t.position.add(right.mul(speed));
        }
        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_W)) {
            t.position.add(new Vector3f(dir).mul(speed));
        }
        if (WindowManager.isKeyDown(GLFW.GLFW_KEY_S)) {
            t.position.sub(new Vector3f(dir).mul(speed));
        }

        float sensitivity = 20.0f * dt;
        c.getCamera().setRotation((float) MouseInput.getDeltaX() * sensitivity, (float) MouseInput.getDeltaY() * sensitivity);
        MouseInput.endFrame();

        ECSWorld.update(dt);

        RenderManager.render(camera);
    }
}