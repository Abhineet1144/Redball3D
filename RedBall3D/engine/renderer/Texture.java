package engine.renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

public class Texture {
    private String filePath;
    private int texId;

    public Texture(String filePath) {
        this.filePath = filePath;

        texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        ByteBuffer textureImg = STBImage.stbi_load(filePath, width, height, channels, 0);

        if (textureImg != null) {
            int format = channels.get(0) == 4 ? GL11.GL_RGBA : GL11.GL_RGB;
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL11.GL_UNSIGNED_BYTE, textureImg);
            STBImage.stbi_image_free(textureImg);
        } else {
            System.err.println("Failed to load texture: " + filePath);
            System.err.println(STBImage.stbi_failure_reason());
        }
    }

    public int getTexID() {
        return texId;
    }

    public String getFilePath() {
        return filePath;
    }
}
