package engine.renderer;

public class MouseInput {
    private static double lastX = 0, lastY = 0;
    private static double deltaX = 0, deltaY = 0;
    private static boolean firstMouse = true;

    public static void onMouseMove(double x, double y) {
        if (firstMouse) {
            lastX = x;
            lastY = y;
            firstMouse = false;
        }

        deltaX = x - lastX;
        deltaY = lastY - y;
        lastX = x;
        lastY = y;
    }

    public static double getDeltaX() { return deltaX; }
    public static double getDeltaY() { return deltaY; }

    // Call at end of each frame to reset deltas
    public static void endFrame() {
        deltaX = 0;
        deltaY = 0;
    }
}