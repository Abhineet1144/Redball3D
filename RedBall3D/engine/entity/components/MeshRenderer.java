package engine.entity.components;

import engine.renderer.Texture;

public class MeshRenderer extends Component {
    public Vertex[] vertices;
    public int[] eboVal;
    private Texture texture;

    public MeshRenderer(Vertex[] vertices, int[] eboVal, Texture texture) {
        this.vertices = vertices;
        this.eboVal = eboVal;
        this.texture = texture;
    }

    @Override
    public void update(float dt) {

    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public static class Vertex {
        public float x;
        public float y;
        public float z;
        public float r, g, b, a;
        public float xt, yt;
        public float nx, ny, nz;  // ADD NORMALS

        public Vertex(float x, float y, float z, float r, float g, float b, float a, float xt, float yt, float nx, float ny, float nz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.xt = xt;
            this.yt = yt;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
        }
    }
}