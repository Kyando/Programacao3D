package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.joml.Matrix4f;

import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Mesh;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Window;
import org.joml.Vector3f;

public class RotatingCube implements Scene {
    private Keyboard keys = Keyboard.getInstance();
    
    private Mesh mesh;
    private float angle;
    private Camera camera = new Camera();

    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        mesh = MeshFactory.createCube();
    }

    @Override
    public void update(float secs) {
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }

        if (keys.isDown(GLFW_KEY_A)) {
            camera.rotateTarget((float)Math.toRadians(180) * secs);
        }

        if (keys.isDown(GLFW_KEY_D)) {
            camera.rotateTarget(-(float)Math.toRadians(180) * secs);
        }

        if (keys.isDown(GLFW_KEY_LEFT)){
            camera.strafeLeft();
        }

        if (keys.isDown(GLFW_KEY_RIGHT)){
            camera.strafeRight(new Vector3f(0.1f, 0, 0));
        }


        if (keys.isDown(GLFW_KEY_UP)){
            camera.moveFront(0.05f);
        }

        if (keys.isDown(GLFW_KEY_DOWN)){
            camera.moveFront(-0.05f);
        }

    }

    @Override
    public void draw() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        mesh.getShader().bind()
                .setUniform("uProjection", camera.getProjectionMatrix())
                .setUniform("uView", camera.getViewMatrix())
                .unbind();
        mesh.setUniform("uWorld", new Matrix4f().rotateY(angle));
        mesh.draw();
    }

    @Override
    public void deinit() {
    }

    public static void main(String[] args) {
        new Window(new RotatingCube(), "Rotating cube", 800, 600).show();
    }
}
