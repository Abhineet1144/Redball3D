package engine.utils;

public class AssetPool {
    private static final String VERTEX_SHADER = "#version 330 core\n" + "layout (location = 0) in vec3 aPos;\n" + "layout (location = 1) in vec3 aNormal;\n" + "layout (location = 2) in vec2 aTexCoords;\n" + "\n" + "out vec3 FragPos;\n" + "out vec3 Normal;\n" + "out vec2 TexCoords;\n" + "\n" + "uniform mat4 transform;   // renamed from model to match Java\n" + "uniform mat4 view;\n" + "uniform mat4 projection;\n" + "\n" + "void main()\n" + "{\n" + "\tFragPos = vec3(transform * vec4(aPos, 1.0));\n" + "\tNormal = mat3(transpose(inverse(transform))) * aNormal;\n" + "\tTexCoords = aTexCoords;\n" + "\n" + "\tgl_Position = projection * view * vec4(FragPos, 1.0);\n" + "}";
    private static final String FRAGMENT_SHADER = "#version 330 core\n" + "out vec4 FragColor;\n" + "\n" + "in vec2 TexCoords;\n" + "in vec3 Normal;\n" + "in vec3 FragPos;\n" + "\n" + "struct Light {\n" + "\tvec3 position;\n" + "\tvec3 ambient;\n" + "\tvec3 diffuse;\n" + "\tvec3 specular;\n" + "};\n" + "\n" + "#define NUM_LIGHTS 2\n" + "uniform Light lights[NUM_LIGHTS];\n" + "uniform vec3 viewPos;\n" + "uniform sampler2D texture_diffuse1;\n" + "uniform sampler2D texture_specular1;\n" + "\n" + "void main()\n" + "{\n" + "\tvec3 norm = normalize(Normal);\n" + "\tvec3 viewDir = normalize(viewPos - FragPos);\n" + "\tvec3 result = vec3(0.0);\n" + "\n" + "\tfor (int i = 0; i < NUM_LIGHTS; i++)\n" + "\t{\n" + "\t\t// Ambient\n" + "\t\tvec3 ambient = lights[i].ambient * texture(texture_diffuse1, TexCoords).rgb;\n" + "\n" + "\t\t// Diffuse\n" + "\t\tvec3 lightDir = normalize(lights[i].position - FragPos);\n" + "\t\tfloat diff = max(dot(norm, lightDir), 0.0);\n" + "\t\tvec3 diffuse = lights[i].diffuse * diff * texture(texture_diffuse1, TexCoords).rgb;\n" + "\n" + "\t\t// Specular\n" + "\t\tvec3 reflectDir = reflect(-lightDir, norm);\n" + "\t\tfloat spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);\n" + "\t\tvec3 specular = lights[i].specular * spec * texture(texture_specular1, TexCoords).rgb;\n" + "\n" + "\t\tresult += ambient + diffuse + specular;\n" + "\t}\n" + "\n" + "\tFragColor = vec4(result, 1.0);\n" + "}";

    public static String getFragmentShaderSource() {
        return FRAGMENT_SHADER;
    }

    public static String getVertexShaderSource() {
        return VERTEX_SHADER;
    }
}