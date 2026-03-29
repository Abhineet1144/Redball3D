package engine.renderer;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projection, view;
    private Vector3f position;

    public Camera(Vector3f position) {
        this.position = position;
        this.projection = new Matrix4f();
        this.view = new Matrix4f();
    }

    public void adjustProjection(int width, int height) {
        projection.identity();
        projection.perspective(
                Math.toRadians(45.0f),    // Field of view (45 degrees)
                (float) width / height,          // Aspect ratio
                0.1f,                            // Near clipping plane
                1000.0f                          // Far clipping plane
        );
    }

    public Matrix4f getViewMat() {
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f eye = new Vector3f(position.x, position.y, position.z);
        Vector3f target = new Vector3f(position.x, position.y, 0.0f);

        view.identity();
        view = view.lookAt(eye, target, cameraUp);
        return view;
    }

    public Matrix4f getProjectionMat() {
        return projection;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }
}