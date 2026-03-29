package engine.entity.components;

import engine.renderer.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

// CameraComponent.java
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
        camera.setPosition(new Vector3f(gameObject.getComponent(Transform.class).position.x, gameObject.getComponent(Transform.class).position.y, gameObject.getComponent(Transform.class).position.z));
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