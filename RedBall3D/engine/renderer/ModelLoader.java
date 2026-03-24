package engine.renderer;

import org.lwjgl.assimp.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    public static class Material {
        public String diffuseMap;
        public String normalMap;
        public String specularMap;
        public String roughnessMap;

        public Material() {
            this.diffuseMap = null;
            this.normalMap = null;
            this.specularMap = null;
            this.roughnessMap = null;
        }
    }

    public static class Vertex {
        public float x, y, z;
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

    public static class Mesh {
        public Vertex[] vertices;
        public int[] indices;
        public Material material;

        public Mesh(Vertex[] vertices, int[] indices, Material material) {
            this.vertices = vertices;
            this.indices = indices;
            this.material = material;
        }
    }

    public static List<Mesh> loadModel(String path) {
        AIScene scene = aiImportFile(path,
                aiProcess_Triangulate |
                        aiProcess_FlipUVs |
                        aiProcess_GenNormals |
                        aiProcess_JoinIdenticalVertices
        );

        if (scene == null || scene.mRootNode() == null) {
            throw new RuntimeException("Failed to load model: " + aiGetErrorString());
        }

        String modelDir = new File(path).getParent();
        if (modelDir == null) modelDir = "";

        // Load all materials
        Map<Integer, Material> materials = loadMaterials(scene, modelDir);

        List<Mesh> meshes = new ArrayList<>();
        int numMeshes = scene.mNumMeshes();

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(scene.mMeshes().get(i));
            int materialIndex = aiMesh.mMaterialIndex();
            Material material = materials.getOrDefault(materialIndex, new Material());
            meshes.add(processMesh(aiMesh, material));
        }

        aiReleaseImport(scene);
        return meshes;
    }

    private static Map<Integer, Material> loadMaterials(AIScene scene, String modelDir) {
        Map<Integer, Material> materials = new HashMap<>();
        int numMaterials = scene.mNumMaterials();

        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(scene.mMaterials().get(i));
            Material material = new Material();

            // Diffuse texture (map_Kd)
            material.diffuseMap = getTexturePath(aiMaterial, aiTextureType_DIFFUSE, modelDir);

            // Normal map (map_Bump or map_bump)
            material.normalMap = getTexturePath(aiMaterial, aiTextureType_NORMALS, modelDir);
            if (material.normalMap == null) {
                material.normalMap = getTexturePath(aiMaterial, aiTextureType_HEIGHT, modelDir);
            }

            // Specular map (map_Ks)
            material.specularMap = getTexturePath(aiMaterial, aiTextureType_SPECULAR, modelDir);

            // Roughness/Shininess map
            material.roughnessMap = getTexturePath(aiMaterial, aiTextureType_SHININESS, modelDir);

            materials.put(i, material);

            // Debug output
            System.out.println("=== Material " + i + " ===");
            if (material.diffuseMap != null) System.out.println("  Diffuse: " + material.diffuseMap);
            if (material.normalMap != null) System.out.println("  Normal: " + material.normalMap);
            if (material.specularMap != null) System.out.println("  Specular: " + material.specularMap);
            if (material.roughnessMap != null) System.out.println("  Roughness: " + material.roughnessMap);
        }

        return materials;
    }

    private static String getTexturePath(AIMaterial material, int textureType, String modelDir) {
        AIString texPath = AIString.calloc();
        int result = aiGetMaterialTexture(
                material,
                textureType,
                0,
                texPath,
                (int[]) null,
                null,
                null,
                null,
                null,
                null
        );

        if (result == aiReturn_SUCCESS) {
            String textureFile = texPath.dataString();
            textureFile = textureFile.replace("\\", "/");

            String fullPath;
            if (!textureFile.contains("/")) {
                fullPath = modelDir + "/" + textureFile;
            } else {
                fullPath = textureFile;
            }

            texPath.free();

            // Check if file exists
            if (new File(fullPath).exists()) {
                return fullPath;
            }
        }

        texPath.free();
        return null;
    }

    private static Mesh processMesh(AIMesh aiMesh, Material material) {
        List<Vertex> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();  // ADD THIS
        AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
        AIColor4D.Buffer aiColors = aiMesh.mColors(0);

        for (int i = 0; i < aiMesh.mNumVertices(); i++) {
            AIVector3D vertex = aiVertices.get(i);

            float x = vertex.x();
            float y = vertex.y();
            float z = vertex.z();

            // Normals
            float nx = 0.0f, ny = 1.0f, nz = 0.0f;  // Default up
            if (aiNormals != null) {
                AIVector3D normal = aiNormals.get(i);
                nx = normal.x();
                ny = normal.y();
                nz = normal.z();
            }

            float u = 0.0f, v = 0.0f;
            if (aiTexCoords != null) {
                AIVector3D texCoord = aiTexCoords.get(i);
                u = texCoord.x();
                v = 1.0f - texCoord.y();
            }

            float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
            if (aiColors != null) {
                AIColor4D color = aiColors.get(i);
                r = color.r();
                g = color.g();
                b = color.b();
                a = color.a();
            }

            vertices.add(new Vertex(x, y, z, r, g, b, a, u, v, nx, ny, nz));
        }

        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiFaces.get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                indices.add(face.mIndices().get(j));
            }
        }

        Vertex[] vertexArray = vertices.toArray(new Vertex[0]);
        int[] indexArray = indices.stream().mapToInt(Integer::intValue).toArray();

        return new Mesh(vertexArray, indexArray, material);
    }
}