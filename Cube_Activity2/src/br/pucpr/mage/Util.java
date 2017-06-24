package br.pucpr.mage;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
	private static String readInputStream(InputStream is) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))){
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException("Unable to load shader", e);
		}
	}
	
	public static int loadProgram(String ...shaders) {
		int[] ids = new int[shaders.length];
		for (int i = 0; i < shaders.length; i++) {
			ids[i] = loadShader(shaders[i]);
		}
		return linkProgram(ids);
	}
	
	public static int loadShader(String name) {
		name = "/br/pucpr/resource/" + name.toLowerCase();
		int type;
		
		if (name.endsWith(".vert") || name.endsWith(".vs"))
			type = GL_VERTEX_SHADER;
		else if (name.endsWith(".frag") || name.endsWith(".fs"))
			type = GL_FRAGMENT_SHADER;
		else if (name.endsWith(".geom") || name.endsWith(".gs"))
			type = GL_GEOMETRY_SHADER;
		else throw new IllegalArgumentException("Invalid shader name: " + name);
		 
		String code = readInputStream(Util.class.getResourceAsStream(name));
		return compileShader(type, code);
	}
	
	public static int compileShader(int type, String code) {
		int shader = glCreateShader(type);
		glShaderSource(shader, code);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			String typeStr = type == GL_VERTEX_SHADER ? "vertex" : type == GL_FRAGMENT_SHADER ? "fragment" : "geometry";			
			throw new RuntimeException("Unable to compile " + typeStr + " shader." + glGetShaderInfoLog(shader));
		}
		return shader;
	}

	public static int linkProgram(int... shaders) {
		int program = glCreateProgram();
		for (int shader : shaders) {
			glAttachShader(program, shader);
		}

		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException("Unable to link shaders." + glGetProgramInfoLog(program));
		}

		for (int shader : shaders) {
			glDetachShader(program, shader);
			glDeleteShader(shader);
		}

		return program;
	}
}
