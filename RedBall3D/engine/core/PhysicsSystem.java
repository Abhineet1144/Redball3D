package redball.engine.core;

import org.dyn4j.dynamics.Body;
import org.dyn4j.world.World;

public class PhysicsSystem {
    public static final float PPM = 32.0f;
    private static final World<Body> world = new World<>();

    public static World<Body> getWorld()  { return world; }
    public static void update(float dt)   { world.update((double) dt); }
    public static void clear()            { world.removeAllBodies(); }
}
