package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import br.pucpr.mage.Scene;
import br.pucpr.mage.Util;
import br.pucpr.mage.Window;

public class RotatingSquare implements Scene {
	private float angle;
	private float angle2;
	
	private int positions;
	private int colors;
	private int indices;
	private int shader;
	private int vao;

	@Override
	public void init() {
		glClearColor(0.0f, 0.0f, 0.2f, 1.0f);

		float[] positionData = new float[] { 
			 -0.5f,  0.5f,   //Vertice 0
			  0.5f,  0.5f,   //Vertice 1
			 -0.5f, -0.5f,   //Vertice 2
			  0.5f, -0.5f,   //Vertice 3
                -0.5f, 0.5f,  //Vertice 4
                -0.5f, -0.5f,  //Vertice 5
                -0.7f, -0.2f, //Vertice 6
                -0.7f, 0.7f, //Vertice 7
                -0.5f, 0.5f,  //Vertice 8
                0.5f, 0.5f,  //Vertice 9
                -0.7f, 0.7f,  //Vertice 10
                0.3f, 0.7f  //Vertice 11
		};
		
		float[] colorData = new float[] {
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f
		};
		
		int indexData[] = new int[] {
			0, 2, 3,
			0, 3, 1,
                4, 6, 5,
                7, 6, 4,
                8, 9, 10,
                11, 10, 9
		};
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		//Atribuição dos vértices
		FloatBuffer data = BufferUtils.createFloatBuffer(positionData.length);
		data.put(positionData).flip();		
		positions = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		
		//Atribuição das cores
		data = BufferUtils.createFloatBuffer(colorData.length);
		data.put(colorData).flip();		
		colors = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colors);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		
		//Atribuição dos índices
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indexData.length);
		indexBuffer.put(indexData).flip();		
		indices = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
		
		
		//Carga/Compilação dos shaders
		shader = Util.loadProgram("basic.vert", "basic.frag");
		
		//Faxina
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	@Override
	public void update(float secs) {
		//angle += Math.toRadians(180) * secs;
	}

	@Override
	public void draw() {		
		glClear(GL_COLOR_BUFFER_BIT);
		
		glUseProgram(shader);
		glBindVertexArray(vao);

		FloatBuffer transform = BufferUtils.createFloatBuffer(16);
		new Matrix4f().rotateY(angle).rotateX(angle2).get(transform);
		
		int uWorld = glGetUniformLocation(shader, "uWorld");		
		glUniformMatrix4fv(uWorld, false, transform);
		
		//Posição dos vertices
		int aPosition = glGetAttribLocation(shader, "aPosition");
		glEnableVertexAttribArray(aPosition);
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 0, 0);
		
		//Cores
		int aColor = glGetAttribLocation(shader, "aColor");
		glEnableVertexAttribArray(aColor);		
		glBindBuffer(GL_ARRAY_BUFFER, colors);
		glVertexAttribPointer(aColor, 3, GL_FLOAT, false, 0, 0);
		
		//Indices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices);
		glDrawElements(GL_TRIANGLES, 18, GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(aPosition);
		glDisableVertexAttribArray(aColor);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
		glUseProgram(0);
	}

	@Override
	public void deinit() {
	}

	public static void main(String[] args) {
		new Window(new RotatingSquare()).show();
	}

	@Override
	public void keyPressed(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
			glfwSetWindowShouldClose(window, GLFW_TRUE);
		if(key == 'a' || key == 'A'){
			angle += Math.toRadians(1);
		}
		if(key == 'd' || key == 'D'){
			angle -= Math.toRadians(1);
		}
        if(key == 'w' || key == 'W'){
            angle2 += Math.toRadians(1);
        }
        if(key == 's' || key == 'S'){
            angle2 -= Math.toRadians(1);
        }
	}
}
