//package engine.scene;
//
//import engine.entity.ECSWorld;
//import engine.entity.GameObject;
//import engine.entity.components.MeshRenderer;
//import engine.entity.components.Transform3D;
//import engine.renderer.*;
//import engine.renderer.mesh.MeshBatchRenderer;
//import org.joml.Vector3f;
//
//import java.util.List;
//
///**
// * Default 3-D scene.
// *
// * Contents
// * --------
// *   • Red ball   – procedural UV-sphere, spins 30 deg/sec
// *   • Ground     – grey plane
// *   • Orbiting point-light (white)
// *
// * To use a real OBJ file instead of the procedural sphere, set:
// *   ballMesh.modelPath = "assets/redball.obj";
// * and remove the uploadProceduralMesh() call for that object.
// * RenderManager3D.prepare() will pick up the path automatically.
// */
//public class GameScene extends AbstractScene {
//
//    private Camera3D     camera;
//    private Transform3D  ballTransform;
//    private float        lightAngle = 0f;
//
//    private static final float SPIN_SPEED    = 30f;   // degrees/sec
//    private static final float LIGHT_RADIUS  = 10f;
//    private static final float LIGHT_HEIGHT  = 8f;
//    private static final float LIGHT_SPEED   = 45f;   // degrees/sec
//
//    @Override public String getName() { return "GameScene"; }
//
//    // -------------------------------------------------------------------------
//
//    @Override
//    public void init() {
//        System.out.println("[GameScene] init()");
//
//        // ── camera ────────────────────────────────────────────────────────────
//        camera = new Camera3D();
//        camera.setOrbitDistance(7f);
//        camera.orbit(0f, 15f);
//        WindowManager.setCamera3D(camera);
//
//        // ── red ball ──────────────────────────────────────────────────────────
//        GameObject ball = ECSWorld.createGameObject("RedBall");
//
//        ballTransform = new Transform3D(
//                new Vector3f(0f, 1f, 0f),
//                new Vector3f(0f, 0f, 0f),
//                new Vector3f(1f, 1f, 1f)
//        );
//        ball.addComponent(ballTransform);
//
//        MeshRenderer ballMesh = new MeshRenderer("");
//        uploadProcedural(ballMesh,
//                ProceduralMeshFactory.sphere(64, 32, 1f, 0.85f, 0.08f, 0.08f));
//        ball.addComponent(ballMesh);
//
//        // ── ground plane ──────────────────────────────────────────────────────
//        GameObject ground = ECSWorld.createGameObject("Ground");
//        ground.addComponent(new Transform3D());          // identity transform
//
//        MeshRenderer groundMesh = new MeshRenderer("");
//        uploadProcedural(groundMesh,
//                ProceduralMeshFactory.plane(16f, 16f, 0.22f, 0.22f, 0.25f));
//        ground.addComponent(groundMesh);
//
//        // ── light ─────────────────────────────────────────────────────────────
//        RenderManager3D.lightColor.set(1f, 0.95f, 0.88f);
//        syncLight();
//
//        System.out.println("[GameScene] init() done.");
//    }
//
//    @Override
//    public void update(float dt) {
//        // spin the ball
//        ballTransform.rotate(0f, SPIN_SPEED * dt, 0f);
//
//        // orbit the light
//        lightAngle = (lightAngle + LIGHT_SPEED * dt) % 360f;
//        syncLight();
//    }
//
//    @Override
//    public void cleanup() {
//        System.out.println("[GameScene] cleanup()");
//    }
//
//    // ── helpers ───────────────────────────────────────────────────────────────
//
//    /**
//     * Upload a procedural mesh list directly into a MeshRenderer,
//     * without going through the file-path code in RenderManager3D.prepare().
//     */
//    private static void uploadProcedural(MeshRenderer mr,
//                                         List<ModelLoader.Mesh> meshes) {
//        MeshBatchRenderer batch = new MeshBatchRenderer();
//        batch.upload(meshes).forEach(mr::addHandle);
//    }
//
//    private void syncLight() {
//        float rad = (float) Math.toRadians(lightAngle);
//        RenderManager3D.lightPosition.set(
//                LIGHT_RADIUS * (float) Math.cos(rad),
//                LIGHT_HEIGHT,
//                LIGHT_RADIUS * (float) Math.sin(rad)
//        );
//    }
//}
