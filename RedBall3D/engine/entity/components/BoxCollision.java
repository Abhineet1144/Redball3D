package engine.entity.components;

import com.bulletphysics.collision.dispatch.CollisionFlags;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;

import javax.vecmath.Vector3f;

public class BoxCollision extends Component {
    private CollisionShape boxShape;

    @Override
    public void start() {
        org.joml.Vector3f scale = gameObject.getComponent(Transform.class).scale;
        boxShape = new BoxShape(new Vector3f(scale.x/2, scale.y/2, scale.z/2));
        com.bulletphysics.dynamics.RigidBody rigidBody = gameObject.getComponent(RigidBody.class).getRigidBody();
        float mass = 1f / rigidBody.getInvMass(); // recover mass
        if (rigidBody.getInvMass() == 0) mass = 0f; // static body
        Vector3f inertia = new Vector3f(0, 0, 0);
        if (mass > 0) boxShape.calculateLocalInertia(mass, inertia);
        rigidBody.setCollisionShape(boxShape);
        rigidBody.setMassProps(mass, inertia);
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
