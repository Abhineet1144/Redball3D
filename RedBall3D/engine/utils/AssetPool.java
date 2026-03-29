package engine.utils;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class AssetPool {
    private static final String VERTEX_SHADER_SRC = CommonUtils.mergePath("assets", "vertexShader.vert");
    private static final String FRAGMENT_SHADER_SRC = CommonUtils.mergePath("assets", "fragmentShader.frag");

    public static String getFragmentShader() {
        try {
            return new String(IOUtils.toByteArray(new FileInputStream(FRAGMENT_SHADER_SRC)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getVertexShader() {
        try {
            return new String(IOUtils.toByteArray(new FileInputStream(VERTEX_SHADER_SRC)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}