package gavrolov.am.opengl3d.model;

import android.opengl.Matrix;

import com.google.android.material.shape.TriangleEdgeTreatment;

import java.util.Vector;

public class Vertex {
    public float x;
    public float y;
    public float z;
    public float w = 1;

    public float colorR;
    public float colorG;
    public float colorB;
    public float colorA;

    public float textureX;
    public float textureY;

    public float normalX = 0;
    public float normalY = 0;
    public float normalZ = 0;

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex(float x, float y, float z, float colorR, float colorG, float colorB, float colorA) {
        this(x, y, z);

        this.colorR = colorR;
        this.colorG = colorG;
        this.colorB = colorB;
        this.colorA = colorA;
    }

    public Vertex(float x, float y, float z, float textureX, float textureY) {
        this(x, y, z);

        this.textureX = textureX;
        this.textureY = textureY;
    }

    public Vertex(float x, float y, float z, float textureX, float textureY, float[] normal) {
        this(x, y, z, textureX, textureY);
        normalX = normal[0];
        normalY = normal[1];
        normalZ = normal[2];
    }
}
