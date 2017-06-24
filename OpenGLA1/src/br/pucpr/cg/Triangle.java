package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import br.pucpr.mage.Keyboard;
import br.pucpr.mage.Scene;
import br.pucpr.mage.Window;

import javax.management.RuntimeErrorException;

public class Triangle implements Scene {

	private static final String VERTEX_SHADER =
			"#version 330\n" +
					"in vec2 aPosition;\n" +
					"void main(){\n" +
					"gl_Position = vec4(aPosition, 0.0, 1.0);\n" +
					"}";

	private static final String FRAGMENT_SHADER =
			"#version 330\n" +
					"out vec4 out_color;\n" +
					"void main(){\n" +
					"out_color = vec4(1.0, 1.0, 0.0, 1.0);\n" +
					"}";


	private int compileShader(int shaderType, String code){
		int shader = glCreateShader(shaderType); //cria um shader
		glShaderSource(shader, code); //armazena a string passada por paramentro no shader
		glCompileShader(shader); //compila o shader

		//testando erros
		if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE){
			throw new RuntimeException("Deu ruim" + glGetShaderInfoLog(shader));
		}
		//retorna o identificador do shader compilado
		return shader;
	}

	public int linkProgram(int... shaders){

		//criando programa, que é uma associação de shaders
		int program = glCreateProgram();
		for (int shader : shaders){
			glAttachShader(program, shader);
		}

		//faz a associação e testa por erros
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE){
			throw new RuntimeException("Deu ruim de novo" +
					glGetProgramInfoLog(program));
		}

		//exclui os shaders associados, que não precisam mais existir independentemente
		for (int shader : shaders){
			glDetachShader(program, shader);
			glDeleteShader(shader);
		}
		//retorna identificador do programa gerado
		return program;
	}

    private Keyboard keys = Keyboard.getInstance();
    private int vao;
    private int positions;
    private int shader;

	@Override
	public void init() {		
		//Define a cor de limpeza da tela
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		vao = glGenVertexArrays(); //criando um mesh na placa de video
		glBindVertexArray(vao); //todos os comandos dados a partir daqui se referirão a esse vao


		float[] vertices = new float[]{  //criando coordenadas de um triângulo
				0.0f, 0.8f,
				-0.5f, 0.3f,
				0.5f, 0.3f,

				-0.5f, 0.3f,
				-0.3f, -0.4f,
				0.5f, 0.3f,

				0.5f, 0.3f,
				-0.3f, -0.4f,
				0.3f, -0.4f
		};

		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(vertices.length); //criando buffer no Java para armazenar o array
		positionBuffer.put(vertices).flip(); //armazenando array no Buffer
		positions = glGenBuffers(); //criando um buffer na placa de víceo
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW); //tipo de buffer, dados a serem armazenados e tipo de desenho

		glBindBuffer(GL_ARRAY_BUFFER, 0); //desfazendo bind com o buffer criado
		glBindVertexArray(0); //desfazendo o bind com o vao criado

		int vs = compileShader(GL_VERTEX_SHADER, VERTEX_SHADER); //compilando vertex shader
		int fs = compileShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER); //compilando fragment shader
		shader = linkProgram(vs, fs);
	}

	@Override
	public void update(float secs) {	
        if (keys.isPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwGetCurrentContext(), GLFW_TRUE);
            return;
        }
	}

	@Override
	public void draw() {

		glClear(GL_COLOR_BUFFER_BIT);
		glUseProgram(shader);
		glBindVertexArray(vao);

		//Associa o buffer "positions" ao atributo "aPosition"
		int aPosition = glGetAttribLocation(shader, "aPosition");
		glEnableVertexAttribArray(aPosition);
		glBindBuffer(GL_ARRAY_BUFFER, positions);
		glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 0, 0);

		//Associa os uniforms
		float angle = (float) Math.toRadians(45);
		FloatBuffer transform = BufferUtils.createFloatBuffer(16);
		new Matrix4f().rotateY(angle).get(transform);

		int uWorld = glGetUniformLocation(shader, "uWorld");
		glUniformMatrix4fv(uWorld, false, transform);

		//Comanda o desenho
		glDrawArrays (GL_TRIANGLES, 0, 9);

		//Faxina
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(aPosition);
		glBindVertexArray(0);
		glUseProgram(0);

	}

	@Override
	public void deinit() {
	}

	public static void main(String[] args) {
		new Window(new Triangle()).show();
	}
}
