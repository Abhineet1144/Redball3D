#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in vec3 aNormal;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoords;
out vec4 Color;

uniform mat4 transform;
uniform mat4 view;
uniform mat4 projection;

void main()
{
    FragPos = vec3(transform * vec4(aPos, 1.0));
    Normal = mat3(transpose(inverse(transform))) * aNormal;
    TexCoords = aTexCoords;
    Color = aColor;
    gl_Position = projection * view * vec4(FragPos, 1.0);
}