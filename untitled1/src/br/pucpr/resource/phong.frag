#version 330

in vec3 vNormal;
in vec3 vViewPath;
out vec4 outColor;

//cor da luz ambiente
uniform vec3 uAmbientMaterial;

//cor da luz difusa
uniform vec3 uDiffuseMaterial;

//cor da luz especular
uniform vec3 uSpecularMaterial;


//cor da luz ambiente
uniform vec3 uAmbientLight;

//cor da luz difusa
uniform vec3 uDiffuseLight;

//direção da luz
in vec3 uLightDir;

//cor da luz especular
uniform vec3 uSpecularLight;

//Sensibilidade do material à luz especular
uniform float uSpecularPower;

float factor = 0.5;

void main() {
    vec3 N = normalize(vNormal);
    vec3 L = normalize(uLightDir);
    float attenuate = 1.0/(length(uLightDir)*factor)*(length(uLightDir)*factor);

    //Calculo do componente ambiente
    vec3 ambient = uAmbientLight * uAmbientMaterial;

    //Calculo do componente difuso
    float intensity = dot(N, -L); //Cosseno do ângulo entre N e L
    vec3 diffuse = clamp(uDiffuseLight * intensity, 0.0, 1.0) * uDiffuseMaterial;

    //Calculo do componente especular
    vec3 specular;
    if(uSpecularPower > 0.0){
        vec3 V = normalize(vViewPath);
        vec3 R = reflect(L, N);
        float specularIntensity = pow(dot(V, R), uSpecularPower);
        specular = specularIntensity * uSpecularLight * uSpecularMaterial;
    }


    //Combina os componentes
    vec3 color = clamp(ambient + diffuse + specular, 0.0, 1.0) * attenuate;

    outColor = vec4(color, 1.0f);
}