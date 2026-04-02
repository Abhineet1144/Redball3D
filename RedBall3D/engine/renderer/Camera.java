package engine.renderer;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projection, view;
    private Vector3f position;
    public Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
    public Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private float yaw = -90.0f;
    private float pitch = 0.0f;


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

    public void updateFront() {
        front.x = (float)(Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float)(Math.sin(Math.toRadians(pitch)));
        front.z = (float)(Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.normalize();
    }

    public Matrix4f getViewMat() {
        Vector3f target = new Vector3f(position).add(front); // always look ahead
        view.identity();
        view = view.lookAt(position, target, cameraUp);
        return view;
    }

    public Matrix4f getProjectionMat() {
        return projection;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(float deltaYaw, float deltaPitch) {
        yaw += deltaYaw;
        pitch += deltaPitch;

        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;

        updateFront();
    }
}