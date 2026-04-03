package engine.entity.components;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import engine.core.PhysicsSystem;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class RigidBody extends Component{

    private com.bulletphysics.dynamics.RigidBody rigidBody;
    private CollisionShape collisionShape;

    @Override
    public void start() {
        collisionShape = new SphereShape(0.001f);
        org.joml.Vector3f pos = gameObject.getComponent(Transform.class).position;

        DefaultMotionState sphereMotionState = new DefaultMotionState(
                new com.bulletphysics.linearmath.Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new javax.vecmath.Vector3f(pos.x, pos.y, pos.z), 1.0f) )
        );
        javax.vecmath.Vector3f sphereInertia = new javax.vecmath.Vector3f(0, 0, 0);
        collisionShape.calculateLocalInertia(1f, sphereInertia);
        RigidBodyConstructionInfo constructionInfo = new RigidBodyConstructionInfo(
                1f, sphereMotionState, collisionShape, sphereInertia
        );

        rigidBody = new com.bulletphysics.dynamics.RigidBody(constructionInfo);
        rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | CollisionFlags.NO_CONTACT_RESPONSE);

        PhysicsSystem.getDynamicsWorld().addRigidBody(rigidBody);
    }

    @Override
    public void update(float dt) {

    }

    public void setStatic() {
        rigidBody.setMassProps(0f, new Vector3f(0, 0, 0));
        rigidBody.setCollisionFlags(CollisionFlags.STATIC_OBJECT);
        rigidBody.updateInertiaTensor();
        rigidBody.activate(true);
    }

    public com.bulletphysics.dynamics.RigidBody getRigidBody() {
        return rigidBody;
    }

    public void setCollisionShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public CollisionShape getCollisionShape() {
        return collisionShape;
    }
}