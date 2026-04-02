package engine.entity.components;

import engine.renderer.ModelLoader;
import engine.renderer.texture.Texture;

public class MeshRenderer extends Component {
    public ModelLoader.ModelData data;

    public MeshRenderer(ModelLoader.ModelData data) {
        this.data = data;
    }

    @Override
    public void update(float dt) {

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