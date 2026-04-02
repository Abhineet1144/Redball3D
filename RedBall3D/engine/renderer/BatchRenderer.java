package engine.renderer;

import java.util.ArrayList;
import java.util.List;

import engine.entity.GameObject;
import engine.entity.components.MeshRenderer;
import engine.entity.components.Transform;
import engine.renderer.texture.Texture;
import engine.utils.AssetPool;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class BatchRenderer {
    public static final int MAX_ENTITIES = 1000;
    private static final int POS_SIZE = 3 * Float.BYTES;
    private static final int COLOR_SIZE = 4 * Float.BYTES;
    private static final int TEXTURE_MAP_SIZE = 2 * Float.BYTES;
    private static final int NORMAL_SIZE = 3 * Float.BYTES;  // ADD THIS
    private static final int OVERALL_SIZE = POS_SIZE + COLOR_SIZE + TEXTURE_MAP_SIZE + NORMAL_SIZE;  // UPDATED!
    private Shader shader;
    private List<GameObject> entities;
    private float[] verticesData = new float[OVERALL_SIZE * MAX_ENTITIES * 4];
    private List<Texture> diffuseTextures = new ArrayList<>();
    private List<Texture> normalTextures = new ArrayList<>();
    private List<Texture> specularTextures = new ArrayList<>();
    private List<Texture> heightTextures = new ArrayList<>();
    private List<Texture> roughnessTextures = new ArrayList<>();
    public int entityCount = 0;
    private List<Float> verticesDataList = new ArrayList<>();
    private List<Integer> vertexIndexList = new ArrayList<>();
    private int highest = 0;
    private int indexCount;
    private int vao;

    public BatchRenderer(List<GameObject> go) {
        this.shader = new Shader(AssetPool.getVertexShader(), AssetPool.getFragmentShader());
        entities = new ArrayList<>();
        for (GameObject g : go) {
            if (g.getComponent(MeshRenderer.class) != null) {
                entities.add(g);
            }
        }
        entityCount = entities.size();
    }

    public int updateAllVertices() {
        verticesDataList.clear();
        vertexIndexList.clear();
        highest = 0;
        int currentVertexCount = 0;
        for (GameObject go : entities) {
            MeshRenderer mr = go.getComponent(MeshRenderer.class);
            if (mr.data.diffusePath != null) diffuseTextures.add(new Texture(mr.data.diffusePath));
            if (mr.data.normalPath != null) normalTextures.add(new Texture(mr.data.normalPath));
            if (mr.data.specularPath != null) specularTextures.add(new Texture(mr.data.specularPath));
            if (mr.data.heightPath != null) heightTextures.add(new Texture(mr.data.heightPath));
            if (mr.data.roughnessPath != null) roughnessTextures.add(new Texture(mr.data.roughnessPath));
            indexCount += mr.data.indices.length;
            for (int i = 0; i < mr.data.vertices.length; i++) {
                MeshRenderer.Vertex vertex = mr.data.vertices[i];
                Transform transform = go.getComponent(Transform.class);
                Vector4f result = transform.getMatrix().transform(new Vector4f(vertex.x, vertex.y, vertex.z, 1));
                verticesDataList.add(result.x);
                verticesDataList.add(result.y);
                verticesDataList.add(result.z);
                verticesDataList.add(vertex.r);
                verticesDataList.add(vertex.g);
                verticesDataList.add(vertex.b);
                verticesDataList.add(vertex.a);
                verticesDataList.add(vertex.xt);
                verticesDataList.add(vertex.yt);
                verticesDataList.add(vertex.nx);
                verticesDataList.add(vertex.ny);
                verticesDataList.add(vertex.nz);
            }
            for (int i : mr.data.indices) {
                vertexIndexList.add(currentVertexCount + i);
            }
            currentVertexCount += mr.data.vertices.length;
        }
        // Convert ArrayLists to arrays
        float[] verticesData = new float[verticesDataList.size()];
        for (int i = 0; i < verticesDataList.size(); i++) {
            verticesData[i] = verticesDataList.get(i);
        }
        int[] vertexIndex = new int[vertexIndexList.size()];
        for (int i = 0; i < vertexIndexList.size(); i++) {
            vertexIndex[i] = vertexIndexList.get(i);
        }
        System.out.println("Vertices: " + (verticesData.length / 12));
        System.out.println("Indices: " + vertexIndex.length);
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int EBO = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesData, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, vertexIndex, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, OVERALL_SIZE, 0);
        glEnableVertexAttribArray(0);
        // Color attribute
        glVertexAttribPointer(1, 4, GL_FLOAT, false, OVERALL_SIZE, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        // Texture coords attribute
        glVertexAttribPointer(2, 2, GL_FLOAT, false, OVERALL_SIZE, 7 * Float.BYTES);
        glEnableVertexAttribArray(2);
        // Normal attribute (NEW!)
        glVertexAttribPointer(3, 3, GL_FLOAT, false, OVERALL_SIZE, 9 * Float.BYTES);
        glEnableVertexAttribArray(3);
        return vao;
    }

    public void prepare() {
        vao = updateAllVertices();
    }

    // Planning to add Texture Atlas: Combine all your textures into one big texture and offset the UVs per model. Complex to set up.
    public void render(Shader shader) {
        shader.use();
        int indexOffset = 0;
        for (int i = 0; i < entities.size(); i++) {
            MeshRenderer mr = entities.get(i).getComponent(MeshRenderer.class);
            // render loop - add missing roughness bind
            if (i < diffuseTextures.size()) {
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, diffuseTextures.get(i).getTexID());
            }
            if (i < normalTextures.size()) {
                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, normalTextures.get(i).getTexID());
            }
            if (i < specularTextures.size()) {
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, specularTextures.get(i).getTexID());
            }
            if (i < heightTextures.size()) {
                glActiveTexture(GL_TEXTURE3);
                glBindTexture(GL_TEXTURE_2D, heightTextures.get(i).getTexID());
            }
            if (i < roughnessTextures.size()) {
                glActiveTexture(GL_TEXTURE4);
                glBindTexture(GL_TEXTURE_2D, roughnessTextures.get(i).getTexID());
            }
            shader.setBool("hasHeightMap", i < heightTextures.size());
            shader.setBool("hasRoughnessMap", i < roughnessTextures.size());
            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, mr.data.indices.length, GL_UNSIGNED_INT, (long) indexOffset * Integer.BYTES);
            indexOffset += mr.data.indices.length;
        }
    }
}