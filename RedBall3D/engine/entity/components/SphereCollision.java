package engine.entity.components;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;

import javax.vecmath.Vector3f;

public class SphereCollision extends Component {
    private CollisionShape sphereShape;

    @Override
    public void start() {
        sphereShape = new SphereShape(gameObject.getComponent(Transform.class).scale.x);
        com.bulletphysics.dynamics.RigidBody rigidBody = gameObject.getComponent(RigidBody.class).getRigidBody();
        float mass = 1f / rigidBody.getInvMass(); // recover mass
        if (rigidBody.getInvMass() == 0) mass = 0f; // static body
        Vector3f inertia = new Vector3f(0, 0, 0);
        if (mass > 0) sphereShape.calculateLocalInertia(mass, inertia);
        rigidBody.setCollisionShape(sphereShape);
        rigidBody.updateInertiaTensor();

        rigidBody.setCollisionFlags(
                rigidBody.getCollisionFlags() & ~CollisionFlags.NO_CONTACT_RESPONSE
        );

        rigidBody.activate(true);
    }

    @Override
    public void update(float dt) {

    }
}
