package engine.entity.components;

import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import engine.core.PhysicsSystem;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class RigidBody extends Component{

    private com.bulletphysics.dynamics.RigidBody rigidBody;

    public RigidBody(RigidBodyConstructionInfo rigidBodyConstructionInfo) {
        rigidBody = new com.bulletphysics.dynamics.RigidBody(rigidBodyConstructionInfo);
        PhysicsSystem.getDynamicsWorld().addRigidBody(rigidBody);
    }

    @Override
    public void update(float dt) {

    }

    public com.bulletphysics.dynamics.RigidBody getRigidBody() {
        return rigidBody;
    }
}
