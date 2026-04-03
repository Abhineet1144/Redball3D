package engine.scene;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import engine.core.PhysicsSystem;
import engine.entity.ECSWorld;
import engine.entity.GameObject;
import engine.entity.components.*;
import engine.renderer.*;

import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class GameScene extends AbstractScene {

    private GameObject camera;
    private GameObject obj;
    private GameObject obj2;

    @Override
    public void start() {

        CollisionShape sphereShape = new SphereShape(1f); // radius = 1
        DefaultMotionState sphereMotionState = new DefaultMotionState(
                new com.bulletphysics.linearmath.Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(0, 50, 0), 1.0f) )
        );
        javax.vecmath.Vector3f sphereInertia = new javax.vecmath.Vector3f(0, 0, 0);
        sphereShape.calculateLocalInertia(1f, sphereInertia); // mass = 1
        RigidBodyConstructionInfo sphereCI = new RigidBodyConstructionInfo(
                1f, sphereMotionState, sphereShape, sphereInertia
        );

        // Static ground plane (infinite, faces upward)
        CollisionShape groundShape = new StaticPlaneShape(new javax.vecmath.Vector3f(0, 1, 0), 0);
        DefaultMotionState groundMotionState = new DefaultMotionState(
                new com.bulletphysics.linearmath.Transform(
                        new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(0, 0, 0), 1.0f)
                )
        );
        RigidBodyConstructionInfo groundCI = new RigidBodyConstructionInfo(
                0f,
                groundMotionState,
                groundShape,
                new javax.vecmath.Vector3f(0, 0, 0)
        );

        camera = ECSWorld.createGameObject("Camera");
        camera.addComponent(new Transform(new Vector3f(1.0f), new Vector3f(), new Vector3f()));
        camera.addComponent(new CameraComponent(1920, 1080));

        obj = ECSWorld.createGameObject("Mesh_BackPack");
        obj.addComponent(new Transform(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f)));
        obj.addComponent(new MeshRenderer(ModelLoader.loadModel("res/plane/plane.obj", false)));

        obj2 = ECSWorld.createGameObject("sofa");
        obj2.addComponent(new Transform(new Vector3f(0.0f, -3.0f, 0.0f), new Vector3f(Math.toRadians(0),Math.toRadians(0   ),Math.toRadians(0)), new Vector3f(2.0f)));
        obj2.addComponent(new MeshRenderer(ModelLoader.loadModel("res/sofa/source/ready.obj", false)));
        obj2.addComponent(new engine.entity.components.RigidBody());

        RenderManager.prepare();
    }

    @Override
    public void update(float dt) {

        com.bulletphysics.linearmath.Transform trans = new com.bulletphysics.linearmath.Transform();
        obj2.getComponent(RigidBody.class).getRigidBody().getMotionState().getWorldTransform(trans);

        obj2.getComponent(Transform.class).position.y = trans.origin.y;

        float speed = 15.0f * dt;
        Transform t = camera.getComponent(Transform.class);
        CameraComponent c = camera.getComponent(CameraComponent.class);
        Vector3f dir = new Vector3f(c.getCamera().front);
        Vector3f up = new Vector3f(c.getCamera().cameraUp);

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