#version 330

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uWorld;
uniform vec3 uLightPos;

uniform vec3 uCameraPosition;

in vec3 aPosition;
in vec3 aNormal;

out vec3 vNormal;
out vec3 vViewPath;
out vec3 uLightDir;

void main() {
    vec4 worldPos = uWorld * vec4(aPosition, 1.0);
    //vec4 l_pos = vec4(0.0, uLightPos);

    gl_Position =  uProjection * uView * worldPos;
    vNormal = (uWorld *vec4(aNormal, 0)).xyz;
    vViewPath = uCameraPosition - worldPos.xyz;
    //uLightDir = vec3(l_pos - vec4(aPosition, 1.0));
    uLightDir = uLightPos - worldPos.xyz;
}