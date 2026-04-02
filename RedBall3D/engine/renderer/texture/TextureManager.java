package engine.renderer.texture;

import java.util.*;

public class TextureManager {
    public static final TreeMap<String, Texture> textureMap = new TreeMap<>();

    private TextureManager() {}

    public static Texture getTexture(String path) {
        if (textureMap.containsKey(path)) {
            return textureMap.get(path);
        } else {
            Texture tex = new Texture(path);
            textureMap.put(path, tex);
            return tex;
        }
    }

    public static Collection<String> listBoundTextures() {
        return textureMap.keySet();
    }

    public static void clear() {
        textureMap.clear();
        Texture.resetSlotCounter();
    }
}