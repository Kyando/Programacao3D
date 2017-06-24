package br.pucpr.cg;

import java.util.Random;

/**
 * Created by Ian Quint Leisner on 6/21/2017.
 */
public class PerlinNoise {

    float[][] noiseBase;
    float[][] noise;
    long seed;
    int width, height;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float[][] getNoise() {
        return noise;
    }

    //Gera Perlin Noise
    PerlinNoise(int width, int height, long seed){
        this.seed = seed;
        this.width = width;
        this.height = height;
        GenerateWhiteNoise();
        GeneratePerlinNoise(7);
    }

    // Gerando valores aleatório para o array de floats com base em um seed dado pelo usuário
    void GenerateWhiteNoise()
    {
        Random random = new Random(seed);
        noiseBase = GetEmptyArray(width, height);

        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                noiseBase[i][j] = (float)random.nextDouble() % 1;
            }
        }
    }

    //Setando os valores iniciais do array para 0
    private float[][] GetEmptyArray(int width, int height) {
        float[][] out = new float[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                out[i][j] = 0;
            }
        }
        return out;
    }

    float[][] GenerateSmoothNoise(int octave)
    {
        int width = noiseBase.length;
        int height = noiseBase[0].length;

        float[][] smoothNoise = GetEmptyArray(width, height);

        int samplePeriod = 1 << octave;
        float sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++)
        {
            //calculate the horizontal sampling indices
            int sample_i0 = (i / samplePeriod) * samplePeriod;
            int sample_i1 = (sample_i0 + samplePeriod) % width; //wrap around
            float horizontal_blend = (i - sample_i0) * sampleFrequency;

            for (int j = 0; j < height; j++)
            {
                //calculate the vertical sampling indices
                int sample_j0 = (j / samplePeriod) * samplePeriod;
                int sample_j1 = (sample_j0 + samplePeriod) % height; //wrap around
                float vertical_blend = (j - sample_j0) * sampleFrequency;

                //blend the top two corners
                float top = Interpolate(noiseBase[sample_i0][sample_j0],
                        noiseBase[sample_i1][sample_j0], horizontal_blend);

                //blend the bottom two corners
                float bottom = Interpolate(noiseBase[sample_i0][sample_j1],
                        noiseBase[sample_i1][sample_j1], horizontal_blend);

                //final blend
                smoothNoise[i][j] = Interpolate(top, bottom, vertical_blend);
            }
        }

        return smoothNoise;
    }

    float Interpolate(float x0, float x1, float alpha)
    {
        return x0 * (1 - alpha) + alpha * x1;
    }

    float[][] GeneratePerlinNoise(int octaveCount)
    {
        int width = noiseBase.length;
        int height = noiseBase[0].length;

        float[][][] smoothNoise = new float[octaveCount][][]; //an array of 2D arrays containing

        float persistance = 0.5f;

        //generate smooth noise
        for (int i = 0; i < octaveCount; i++)
        {
            smoothNoise[i] = GenerateSmoothNoise(i);
        }

        noise = GetEmptyArray(width, height);
        float amplitude = 1.0f;
        float totalAmplitude = 0.0f;

        //blend noise together
        for (int octave = octaveCount - 1; octave >= 0; octave--)
        {
            amplitude *= persistance;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    noise[i][j] += smoothNoise[octave][i][j] * amplitude;
                }
            }
        }

        //normalisation
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                noise[i][j] /= totalAmplitude;
            }
        }

        return noise;
    }
}
