package engine.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.core.Engine;
import engine.entity.GameObject;
import engine.entity.components.MeshRenderer;
import engine.entity.components.Transform;
import engine.renderer.texture.Texture;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class BatchRenderer {
    private static final int POS_SIZE = 3 * Float.BYTES;
    private static final int COLOR_SIZE = 4 * Float.BYTES;
    private static final int TEXTURE_MAP_SIZE = 2 * Float.BYTES;
    private static final int NORMAL_SIZE = 3 * Float.BYTES;
    private static final int OVERALL_SIZE = POS_SIZE + COLOR_SIZE + TEXTURE_MAP_SIZE + NORMAL_SIZE;
    private List<GameObject> entities;
    private Map<String, Texture> textureCache = new HashMap<>();

    public int entityCount = 0;
    private List<Float> verticesDataList = new ArrayList<>();
    private List<Integer> vertexIndexList = new ArrayList<>();
    private int indexCount;
    private int vao;

    public BatchRenderer(List<GameObject> go) {
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
        indexCount = 0;

        int currentVertexCount = 0;
        for (GameObject go : entities) {
            MeshRenderer mr = go.getComponent(MeshRenderer.class);

            indexCount += mr.data.indices.length;
            for (MeshRenderer.Vertex vertex : mr.data.vertices) {
                verticesDataList.add(vertex.x);
                verticesDataList.add(vertex.y);
                verticesDataList.add(vertex.z);
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

    private void bindOrUnbind(int slot, String path) {
        glActiveTexture(slot);
        if (path != null) {
            Texture tex = textureCache.computeIfAbsent(path, Texture::new);
            glBindTexture(GL_TEXTURE_2D, tex.getTexID());
        } else {
            glBindTexture(GL_TEXTURE_2D, 0); // unbind
        }
    }

    public void render(Shader shader) {
        shader.use();
        glBindVertexArray(vao);

        int globalIndexOffset = 0;

        for (GameObject entity : entities) {
            MeshRenderer mr = entity.getComponent(MeshRenderer.class);
            Transform transform = entity.getComponent(Transform.class);
            shader.setMat4f("transform", transform.getMatrix());

            for (ModelLoader.DrawRange range : mr.data.drawRanges) {
                if (range.diffusePath == null) continue;

                bindOrUnbind(GL_TEXTURE0, range.diffusePath);
                bindOrUnbind(GL_TEXTURE1, range.normalPath);
                bindOrUnbind(GL_TEXTURE2, range.specularPath);
                bindOrUnbind(GL_TEXTURE3, range.heightPath);
                bindOrUnbind(GL_TEXTURE4, range.roughnessPath);

                shader.setBool("hasHeightMap",    range.heightPath    != null);
                shader.setBool("hasRoughnessMap", range.roughnessPath != null);

                long offset = (long)(globalIndexOffset + range.indexOffset) * Integer.BYTES;
                glDrawElements(GL_TRIANGLES, range.indexCount, GL_UNSIGNED_INT, offset);
            }
            globalIndexOffset += mr.data.indices.length;
        }
    }
}