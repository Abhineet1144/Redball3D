package engine.renderer;

import java.util.ArrayList;
import java.util.List;

import engine.entity.components.MeshRenderer;
import engine.utils.AssetPool;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class BatchRenderer {
    private static final int POS_SIZE = 3 * Float.BYTES;
    private static final int COLOR_SIZE = 4 * Float.BYTES;
    private static final int TEXTURE_MAP_SIZE = 2 * Float.BYTES;
    private static final int NORMAL_SIZE = 3 * Float.BYTES;  // ADD THIS
    private static final int OVERALL_SIZE = POS_SIZE + COLOR_SIZE + TEXTURE_MAP_SIZE + NORMAL_SIZE;  // UPDATED!
    private Shader shader;
    private List<MeshRenderer> meshRendererList;

    // Use ArrayLists for dynamic sizing
    private List<Float> verticesDataList = new ArrayList<>();
    private List<Integer> vertexIndexList = new ArrayList<>();
    private int highest = 0;

    public BatchRenderer(List<MeshRenderer> meshRendererList) {
        this.meshRendererList = meshRendererList;
        this.shader = new Shader(AssetPool.getVertexShader(), AssetPool.getFragmentShader());
    }

    public int renderAll() {
        verticesDataList.clear();
        vertexIndexList.clear();
        highest = 0;

        int currentVertexCount = 0;

        for (MeshRenderer meshRenderer : meshRendererList) {
            // Add vertices
            for (int i = 0; i < meshRenderer.vertices.length; i++) {
                MeshRenderer.Vertex vertex = meshRenderer.vertices[i];
                verticesDataList.add(vertex.x);
                verticesDataList.add(vertex.y);
                verticesDataList.add(vertex.z);
                verticesDataList.add(vertex.r);
                verticesDataList.add(vertex.g);
                verticesDataList.add(vertex.b);
                verticesDataList.add(vertex.a);
                verticesDataList.add(vertex.xt);
                verticesDataList.add(vertex.yt);
                verticesDataList.add(vertex.nx);  // ADD NORMALS
                verticesDataList.add(vertex.ny);
                verticesDataList.add(vertex.nz);
            }

            // Add indices (offset by current vertex count)
            for (int i : meshRenderer.eboVal) {
                vertexIndexList.add(currentVertexCount + i);
            }

            currentVertexCount += meshRenderer.vertices.length;
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
}