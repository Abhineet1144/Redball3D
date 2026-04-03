package engine.core;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector3f;

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

        dynamicsWorld.setDebugDrawer(new IDebugDraw() {
            private int debugMode = DebugDrawModes.DRAW_WIREFRAME;

            @Override
            public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
                GL20.glUseProgram(0); // disable shader so fixed-function works
                GL11.glBegin(GL11.GL_LINES);
                GL11.glColor3f(color.x, color.y, color.z);
                GL11.glVertex3f(from.x, from.y, from.z);
                GL11.glVertex3f(to.x, to.y, to.z);
                GL11.glEnd();
            }

            @Override
            public void drawContactPoint(Vector3f pointOnB, Vector3f normalOnB,
                                         float distance, int lifeTime, Vector3f color) {
                Vector3f to = new Vector3f();
                to.scaleAdd(distance * 10f, normalOnB, pointOnB);
                drawLine(pointOnB, to, color);
            }

            @Override
            public void reportErrorWarning(String msg) { System.err.println("[Bullet] " + msg); }

            @Override
            public void draw3dText(Vector3f location, String text) {}

            @Override
            public void setDebugMode(int mode) { this.debugMode = mode; }

            @Override
            public int getDebugMode() { return debugMode; }
        });
    }

    public static void update(float dt) {
        dynamicsWorld.stepSimulation(dt, 10);
    }

    public static DiscreteDynamicsWorld getDynamicsWorld() {
        return dynamicsWorld;
    }
}