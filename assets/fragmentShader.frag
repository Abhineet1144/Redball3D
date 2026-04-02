#version 330 core
out vec4 FragColor;

in vec2 TexCoords;
in vec3 Normal;
in vec3 FragPos;
in vec4 Color;

struct Light {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

#define NUM_LIGHTS 2
uniform Light lights[NUM_LIGHTS];
uniform vec3 viewPos;

uniform sampler2D diffuseMap;
uniform sampler2D specularMap;
uniform sampler2D roughnessMap;
uniform sampler2D heightMap;

uniform float heightScale;
uniform bool hasHeightMap;
uniform bool hasRoughnessMap;

vec2 parallaxMapping(vec2 uv, vec3 viewDir) {
    float height = texture(heightMap, uv).r;
    return uv - viewDir.xy / viewDir.z * (height * heightScale);
}

void main()
{
    vec3 viewDir = normalize(viewPos - FragPos);
    vec2 uv = hasHeightMap ? parallaxMapping(TexCoords, viewDir) : TexCoords;

    vec3 norm = normalize(Normal);
    vec3 result = vec3(0.0);

    // Roughness controls shininess — rough = low shininess, smooth = high
    float roughness = hasRoughnessMap ? texture(roughnessMap, uv).r : 0.5;
    float shininess = mix(256.0, 2.0, roughness);

    for (int i = 0; i < NUM_LIGHTS; i++)
    {
        vec3 ambient = lights[i].ambient * texture(diffuseMap, uv).rgb;

        vec3 lightDir = normalize(lights[i].position - FragPos);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = lights[i].diffuse * diff * texture(diffuseMap, uv).rgb;

        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
        vec3 specular = lights[i].specular * spec * texture(specularMap, uv).rgb;

        result += ambient + diffuse + specular;
    }

    FragColor = vec4(result, 1.0) * Color;
}