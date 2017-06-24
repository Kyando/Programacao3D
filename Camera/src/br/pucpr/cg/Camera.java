package br.pucpr.cg;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

/**
 * Created by Ian Quint Leisner on 5/8/2017.
 */
public class Camera {
    int focalDistance = 3;
    private Vector3f position = new Vector3f(0, 0, 2);
    private Vector3f up = new Vector3f(0, 1, 0);
    private Vector3f direction = new Vector3f(0, 0, -1);

    private float fov = (float)Math.toRadians(60);
    private float near = 0.01f;
    private float far = 1000.0f;

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getUp() {
        return up;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getFar() {
        return far;
    }

    public float getFov() {
        return fov;
    }

    public float getNear() {
        return near;
    }


    public void strafeLeft(){
        Vector3f dirCopy = new Vector3f(direction);
        dirCopy.cross(up).normalize().mul(0.1f);
        position.sub(dirCopy);
    }

    public void strafeRight(Vector3f vec){
        Vector3f dirCopy = new Vector3f(direction);
        dirCopy.cross(up).normalize().mul(0.1f);
        position.add(dirCopy);
    }

    public void moveFront(float vel){
        Vector3f dirCopy = new Vector3f(direction);
        dirCopy.normalize().mul(vel);
        position.add(dirCopy);
    }

    public void rotateTarget(float angle){
        new Matrix3f().rotateY(angle).transform(direction);
    }


    public float getAspect(){
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);

        long window = glfwGetCurrentContext();
        glfwGetWindowSize(window, w, h);

        return w.get() / (float) h.get();
    }

    //matriz que define posicao da camera
    public Matrix4f getViewMatrix(){
        Vector3f target = new Vector3f(direction).add(position);
        return new Matrix4f().lookAt(position, target, up);
    }

    //matriz que define angulo da camera
    public Matrix4f getProjectionMatrix(){
        return new Matrix4f().perspective(fov, getAspect(), near, far);
    }



}
