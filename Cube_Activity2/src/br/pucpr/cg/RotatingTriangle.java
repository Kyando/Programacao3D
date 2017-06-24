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

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import br.pucpr.mage.Scene;
import br.pucpr.mage.Util;
import br.pucpr.mage.Window;

public class RotatingTriangle implements Scene {
	private float angle;	
	private int positions;
	private int colors;
	private int shader;
	private int vao;

	@Override
	public void init() {		
		glClearColor(0.0f, 0.0f, 0.2f, 1.0f);

		float[] positionData = new float[] { 
			     0.0f,  0.5f, 
			    -0.5f, -0.5f, 
			     0.5f, -0.5f 
		};
		
		float[] colorData = new float[] {
				1.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 1.0f,
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
		
		//Carga/Compilação dos shaders
		shader = Util.loadProgram("basic.vert", "basic.frag");
		
		//Faxina
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	@Override
	public void update(float secs) {
		angle += Math.toRadians(180) * secs;
	}

	@Override
	public void draw() {		
		glClear(GL_COLOR_BUFFER_BIT);
		
		glUseProgram(shader);
		glBindVertexArray(vao);

		FloatBuffer transform = BufferUtils.createFloatBuffer(16);		
		new Matrix4f().rotateY(angle).get(transform);
		
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
		
		glDrawArrays(GL_TRIANGLES, 0, 3);

		//Faxina
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(aPosition);
		glDisableVertexAttribArray(aColor);		
		glBindVertexArray(0);
		glUseProgram(0);
	}

	@Override
	public void deinit() {
	}

	public static void main(String[] args) {
		new Window(new RotatingTriangle()).show();
	}

	@Override
	public void keyPressed(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
			glfwSetWindowShouldClose(window, GLFW_TRUE); // We will detect this
															// in our rendering
															// loop
	}
}
