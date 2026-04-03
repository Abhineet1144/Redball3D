package engine.core;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;

public class PhysicsSystem {

    private static DiscreteDynamicsWorld dynamicsWorld;

    public static void setup() {
        BroadphaseInterface broadphase = new DbvtBroadphase();

        DefaultCollisionConfiguration collisionConfig = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfig);

        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        dynamicsWorld = new DiscreteDynamicsWorld(
                dispatcher, broadphase, solver, collisionConfig
        );

        dynamicsWorld.setGravity(new javax.vecmath.Vector3f(0, -9.8f, 0));
    }

    public static void update(float dt) {
        dynamicsWorld.stepSimulation(dt, 10);
    }

    public static DiscreteDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }
}