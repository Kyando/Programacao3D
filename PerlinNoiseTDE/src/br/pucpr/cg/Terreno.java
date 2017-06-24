package br.pucpr.cg;

import org.joml.Vector3f;

import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Created by Ian Quint Leisner on 6/21/2017.
 */
public class Terreno {

    private ArrayList<Vector3f> positions;
    private ArrayList<Integer> indexBuffer;
    private ArrayList<Vector3f> normals;

    public ArrayList<Vector3f> getPositions() {
        return positions;
    }

    public ArrayList<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public ArrayList<Vector3f> getNormals() {
        return normals;
    }

    Terreno(float[][] perlin, int width, int height, float high){

        positions = new ArrayList<Vector3f>();
        indexBuffer = new ArrayList<Integer>();
        normals = new ArrayList<Vector3f>();

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                System.out.println(perlin[x][y]*high);
                positions.add(new Vector3f(x, perlin[x][y]*high, y));
            }
        }

        for(int y = 0; y < height - 1; y++){
            for(int x = 0; x < width -1; x++){
                indexBuffer.add(width*y+(x+1));
                indexBuffer.add(width*y+x);
                indexBuffer.add(width*(y+1)+(x+1));

                indexBuffer.add(width*(y+1)+(x+1));
                indexBuffer.add(width*y+x);
                indexBuffer.add(width*(y+1)+x);
            }
        }

        for(int i = 0; i < positions.size(); i++){
            normals.add(new Vector3f());
        }

        for (int i = 0; i < indexBuffer.size(); i += 3) {
            int i1 = indexBuffer.get(i);
            int i2 = indexBuffer.get(i+1);
            int i3 = indexBuffer.get(i+2);

            Vector3f v1 = new Vector3f(positions.get(i1));
            Vector3f v2 = new Vector3f(positions.get(i2));
            Vector3f v3 = new Vector3f(positions.get(i3));

            Vector3f side1 = new Vector3f(v2).sub(v1);
            Vector3f side2 = new Vector3f(v3).sub(v1);

            Vector3f normal = new Vector3f(side1).cross(side2);
            normals.get(i1).add(normal);
            normals.get(i2).add(normal);
            normals.get(i3).add(normal);
        }

        for (Vector3f normal : normals) {
            normal.normalize();
        }

    }
}
