package engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int ID;

    public Shader(String vertexShaderSource, String fragmentShaderSource) {
        int vertex, fragment;
        IntBuffer successBuffer = BufferUtils.createIntBuffer(1);

        vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex, vertexShaderSource);
        glCompileShader(vertex);

        fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment, fragmentShaderSource);
        glCompileShader(fragment);

        glGetShaderiv(vertex, GL_COMPILE_STATUS, successBuffer);
        if (successBuffer.get(0) != 1) {
            assert false : "ERROR::SHADER::VERTEX::COMPILATION_FAILED";
            System.err.println(glGetShaderInfoLog(vertex));
        }
        successBuffer.clear();

        glGetShaderiv(fragment, GL_COMPILE_STATUS, successBuffer);
        if (successBuffer.get(0) != 1) {
            assert false : "ERROR::SHADER::FRAGMENT::COMPILATION_FAILED";
            System.err.println(glGetShaderInfoLog(fragment));
        }
        successBuffer.clear();

        ID = glCreateProgram();
        glAttachShader(ID, vertex);
        glAttachShader(ID, fragment);
        glLinkProgram(ID);

        glGetProgramiv(ID, GL_LINK_STATUS, successBuffer);
        if (successBuffer.get(0) != 1) {
            assert false : "ERROR::SHADER::PROGRAM::LINKING_FAILED";
            System.err.println(glGetShaderInfoLog(ID));
        }

        glDeleteShader(vertex);
        glDeleteShader(fragment);
    }

    public void use() {
        glUseProgram(ID);
    }

    public int getID() {
        return ID;
    }

    public void setMat4f(String name, Matrix4f value) {
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        value.get(matrixBuffer);
        glUniformMatrix4fv(glGetUniformLocation(ID, name), false, matrixBuffer);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(ID, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(ID, name), value);
    }

    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(ID, name), value ? 1 : 0);
    }

    public void setVec3(String name, float x, float y, float z) {
        glUniform3f(glGetUniformLocation(ID, name), x, y, z);
    }

    public void setVec3(String name, Vector3f value) {
        glUniform3f(glGetUniformLocation(ID, name), value.x, value.y, value.z);
    }
}