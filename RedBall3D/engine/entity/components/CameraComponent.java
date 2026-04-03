package engine.entity.components;

import engine.renderer.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraComponent extends Component {
    public Camera camera;
    public boolean isMain;

    public CameraComponent(int width, int height) {
        this.camera = new Camera(new Vector3f(0, 0, 0));
        this.camera.adjustProjection(width, height);
        this.isMain = true;
    }

    @Override
    public void update(float dt) {
        Transform t = gameObject.getComponent(Transform.class);

        camera.setPosition(new Vector3f(t.position.x, t.position.y, t.position.z));
        camera.setRotation(t.rotation.y, t.rotation.x);

    }

    public Matrix4f getViewMatrix() {
        return camera.getViewMat();
    }

    public Matrix4f getProjectionMatrix() {
        return camera.getProjectionMat();
    }

    public boolean isMain() {
        return isMain;
    }

    public Camera getCamera() {
        return camera;
    }
}