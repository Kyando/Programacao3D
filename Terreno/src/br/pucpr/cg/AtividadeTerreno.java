package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import br.pucpr.mage.*;
import org.joml.Matrix4f;

import org.joml.Vector3f;

import java.io.IOException;

public class AtividadeTerreno implements Scene {
    private Keyboard keys = Keyboard.getInstance();

    /*
    A e D - strafe da câmera
    W e S - Mover para frente/para trás
    F e G - Aumentar ou diminuir noise
    Setas direcionais - alterar direção da câmera
    K e L - Alternar entre modo wireframe e normal
     */

    private Terreno terreno;
    private Mesh mesh;
    private float angleX = 0.0f;
    private float angleY = 0.5f;
    private Camera camera = new Camera();
    float altura = 1.0f;
    
    @Override
    public void init() throws IOException {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

        terreno = new Terreno("E:\\img\\opengl\\heights/vampire.jpg", 0.2f);

        mesh = new MeshBuilder().addVector3fAttribute("aPosition", terreno.getPositions())
        .addVector3fAttribute("aNormal", terreno.getNormals())
                .setIndexBuffer(terreno.getIndexBuffer())
                .loadShader("/br/pucpr/resource/phong")
                .create();
        
    }

    @Override
    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }

        float velocity = 30.0f*secs;
        float angleVel = 2.0f * secs;

        if (keys.isDown(GLFW_KEY_A)) {
            camera.strafeLeft(velocity);
        }

        if (keys.isDown(GLFW_KEY_D)) {
            camera.strafeRight(velocity);
        }
        
        if (keys.isDown(GLFW_KEY_W)) {
            camera.moveFront(velocity);
        }

        if (keys.isDown(GLFW_KEY_S)) {
            camera.moveFront(-velocity);
        }

        if (keys.isDown(GLFW_KEY_LEFT)) {
            camera.rotateY(angleVel);
        }

        if (keys.isDown(GLFW_KEY_RIGHT)) {
            camera.rotateY(-angleVel);
        }

        if (keys.isDown(GLFW_KEY_UP)) {
            camera.rotateX(angleVel);
        }

        if (keys.isDown(GLFW_KEY_DOWN)) {
            camera.rotateX(-angleVel);
        }
    }

    public float clamp(){
        return 0;
    }

@Override
public void draw() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    Shader shader = mesh.getShader();
    mesh.getShader()
            .bind()
            .setUniform("uProjection", camera.getProjectionMatrix())
            .setUniform("uView", camera.getViewMatrix())
            .setUniform("uCameraPosition", camera.getPosition())
            .setUniform("uLightDir", new Vector3f(1.0f, -1.0f, 1.05f))
            .setUniform("uDiffuseLight", new Vector3f(1.0f, 1.0f, 0.8f))
            .setUniform("uAmbientLight", new Vector3f(0.05f, 0.05f, 0.05f))
            .setUniform("uSpecularLight", new Vector3f(1.0f, 1.0f, 1.0f))
            .setUniform("uDiffuseMaterial", new Vector3f(1.0f, 1.0f, 1.0f))
            .setUniform("uAmbientMaterial", new Vector3f(1.0f, 1.0f, 1.0f))
            .setUniform("uSpecularMaterial", new Vector3f(1.0f, 1.0f, 1.0f))
            .setUniform("uSpecularPower", 10.0f)
            .setUniform("uAltura", altura);
    shader.unbind();

    mesh.setUniform("uWorld", new Matrix4f().rotateY(0));
    mesh.draw();
}

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new AtividadeTerreno(), "Terreno", 800, 600).show();
    }
}
