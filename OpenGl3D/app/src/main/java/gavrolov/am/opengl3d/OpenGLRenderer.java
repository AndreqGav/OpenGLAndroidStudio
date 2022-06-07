package gavrolov.am.opengl3d;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glViewport;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import gavrolov.am.opengl3d.model.ModelObject;
import gavrolov.am.opengl3d.model.Scene;
import gavrolov.am.opengl3d.model.Texture;
import gavrolov.am.opengl3d.model.Vertex;
import gavrolov.am.opengl3d.utils.ProgramUtils;

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    private final Context context;
    private Scene scene;

    private static final float TIME = 100f;

    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);

        scene = new Scene();

        if (true) {
            scene.CAMERA_TYPE = 1;
            scene.setCameraPosition(-4, 0, 7, 0, 0, 0);
            scene.setLightPosition(4f, 2f, 0.5f);
            scene.modelObjects = get3DObjects();
        } else {
            scene.CAMERA_TYPE = 3;
            scene.setCameraPosition(0, 0, 10, 0, 0, 0);
            scene.setLightPosition(0f, 0f, -5f);
            scene.modelObjects = get2DObjects();
        }

        scene.createBuffers();
        scene.bindBuffers();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);
        scene.updateView(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLineWidth(10);
        scene.drawObjects();

        float time = (float)(SystemClock.uptimeMillis() % TIME) / TIME;

        Matrix.rotateM(scene.getViewMatrix(), 0, time, 0, 1f, 0);
    }

    private ModelObject[] get3DObjects() {
        Texture floor1 = new Texture(context, R.drawable.floor1);
        Texture desk1 = new Texture(context, R.drawable.desk);
        Texture brick = new Texture(context, R.drawable.brick);

        float[] zNormal = new float[]{0, 0, 1};
        float[] mzNormal = new float[]{0, 0, -1};
        float[] yNormal = new float[]{0, 1, 0};
        float[] xNormal = new float[]{1, 0, 0};
        float[] mxNormal = new float[]{-1, 0, 0};

        int vertexShaderRawId = R.raw.vertex_shader;
        int fragmentShaderRawId = R.raw.fragment_shader;

        ModelObject plane = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLE_STRIP, floor1,
                new Vertex[]{
                        // face
                        new Vertex(-2.5f, -3f, 2f, 0.0f, 0.0f, zNormal),
                        new Vertex(2.5f, -3f, 2f, 1.0f, 0.0f, zNormal),
                        new Vertex(-2.5f, -3.5f, 2f, 0.0f, 1.0f, zNormal),
                        new Vertex(2.5f, -3.5f, 2f, 1f, 1.0f, zNormal),

                        // back
                        new Vertex(-2.5f, -3f, -2f, 0.0f, 0.0f, mzNormal),
                        new Vertex(2.5f, -3f, -2f, 1.0f, 0.0f, mzNormal),
                        new Vertex(-2.5f, -3.5f, -2f, 0.0f, 1.0f, mzNormal),
                        new Vertex(2.5f, -3.5f, -2f, 1f, 1.0f, mzNormal),

                        // up
                        new Vertex(-2.5f, -3f, 2f, 0.0f, 0.0f, yNormal),
                        new Vertex(2.5f, -3f, 2f, 1.0f, 0.0f, yNormal),
                        new Vertex(-2.5f, -3f, -2f, 0.0f, 1.0f, yNormal),
                        new Vertex(2.5f, -3f, -2f, 1f, 1.0f, yNormal),

                        // left
                        new Vertex(-2.5f, -3f, 2f, 0.0f, 0.0f, yNormal),
                        new Vertex(-2.5f, -3f, -2f, 0.0f, 1.0f, mxNormal),
                        new Vertex(-2.5f, -3.5f, 2f, 1.0f, 0.0f, mxNormal),
                        new Vertex(-2.5f, -3.5f, -2f, 1f, 1.0f, mxNormal),

                        // right
                        new Vertex(2.5f, -3f, 2f, 0.0f, 0.0f, xNormal),
                        new Vertex(2.5f, -3.5f, 2f, 1.0f, 0.0f, xNormal),
                        new Vertex(2.5f, -3f, -2f, 0.0f, 1.0f, xNormal),
                        new Vertex(2.5f, -3.5f, -2f, 1f, 1.0f, xNormal),
                });

        ModelObject deskPlane = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLE_STRIP, desk1,
                new Vertex[]{
                        // face
                        new Vertex(-2f, -1f, 1f, 0.0f, 0.0f, zNormal),
                        new Vertex(2f, -1f, 1f, 1.0f, 0.0f, zNormal),
                        new Vertex(-2f, -1.1f, 1f, 0.0f, 1.0f, zNormal),
                        new Vertex(2f, -1.1f, 1f, 1f, 1.0f, zNormal),

                        // back
                        new Vertex(-2f, -1f, -1f, 0.0f, 0.0f, mzNormal),
                        new Vertex(2f, -1f, -1f, 1.0f, 0.0f, mzNormal),
                        new Vertex(-2f, -1.1f, -1f, 0.0f, 1.0f, mzNormal),
                        new Vertex(2f, -1.1f, -1f, 1f, 1.0f, mzNormal),

                        // up
                        new Vertex(-2f, -1f, 1f, 0.0f, 0.0f, yNormal),
                        new Vertex(2f, -1f, 1f, 1.0f, 0.0f, yNormal),
                        new Vertex(-2f, -1f, -1f, 0.0f, 1.0f, yNormal),
                        new Vertex(2f, -1f, -1f, 1f, 1.0f, yNormal),

                        // left
                        new Vertex(-2f, -1f, 1f, 0.0f, 0.0f, yNormal),
                        new Vertex(-2f, -1.1f, 1f, 1.0f, 0.0f, mxNormal),
                        new Vertex(-2f, -1f, -1f, 0.0f, 1.0f, mxNormal),
                        new Vertex(-2f, -1.1f, -1f, 1f, 1.0f, mxNormal),

                        // right
                        new Vertex(2f, -1f, 1f, 0.0f, 0.0f, xNormal),
                        new Vertex(2f, -1.1f, 1f, 1.0f, 0.0f, xNormal),
                        new Vertex(2f, -1f, -1f, 0.0f, 1.0f, yNormal),
                        new Vertex(2f, -1.1f, -1f, 1f, 1.0f, xNormal),
                });

        ModelObject leg1 = GenDeskLeg(0, 0);
        ModelObject leg2 = GenDeskLeg(3.9f, 0);
        ModelObject leg3 = GenDeskLeg(0, -1.9f);
        ModelObject leg4 = GenDeskLeg(3.9f, -1.9f);

        ModelObject wallPlane = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLE_STRIP, brick,
                new Vertex[]{
                        // face
                        new Vertex(-2.5f, 3f, -1.8f, 0.0f, 0.0f, zNormal),
                        new Vertex(2.5f, 3f, -1.8f, 1.0f, 0.0f, zNormal),
                        new Vertex(-2.5f, -3f, -1.8f, 0.0f, 1.0f, zNormal),
                        new Vertex(2.5f, -3f, -1.8f, 1f, 1.0f, zNormal),

                        // back
                        new Vertex(-2.5f, 3f, -2f, 0.0f, 0.0f, mzNormal),
                        new Vertex(2.5f, 3f, -2f, 1.0f, 0.0f, mzNormal),
                        new Vertex(-2.5f, -3f, -2f, 0.0f, 1.0f, mzNormal),
                        new Vertex(2.5f, -3f, -2f, 1f, 1.0f, mzNormal),

                        // left
                        new Vertex(-2.5f, 3f, -1.8f, 0.0f, 0.0f, mxNormal),
                        new Vertex(-2.5f, -3f, -1.8f, 0.0f, 1.0f, mxNormal),
                        new Vertex(-2.5f, 3f, -2f, 1.0f, 0.0f, mxNormal),
                        new Vertex(-2.5f, -3f, -2f, 1f, 1.0f, mxNormal),

                        // right
                        new Vertex(2.5f, 3f, -1.8f, 0.0f, 0.0f, xNormal),
                        new Vertex(2.5f, -3f, -1.8f, 0.0f, 1.0f, xNormal),
                        new Vertex(2.5f, 3f, -2f, 1.0f, 0.0f, xNormal),
                        new Vertex(2.5f, -3f, -2f, 1f, 1.0f, xNormal),
                });

        return new ModelObject[]{plane, wallPlane, deskPlane, leg1, leg2, leg3, leg4};
    }

    private ModelObject GenDeskLeg(float offsetX, float offsetZ) {
        Texture desk1 = new Texture(context, R.drawable.box);

        float[] zNormal = new float[]{0, 0, 1};
        float[] mzNormal = new float[]{0, 0, -1};
        float[] xNormal = new float[]{1, 0, 0};
        float[] mxNormal = new float[]{-1, 0, 0};

        int vertexShaderRawId = R.raw.vertex_shader;
        int fragmentShaderRawId = R.raw.fragment_shader;

        return new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLE_STRIP, desk1,
                new Vertex[]{
                        // face
                        new Vertex(-2f + offsetX, -1.1f + (float) 0, offsetZ + 1f, 0.0f, 0.0f, zNormal),
                        new Vertex(-1.9f + offsetX, -1.1f + (float) 0, offsetZ + 1f, 1.0f, 0.0f, zNormal),
                        new Vertex(-2f + offsetX, -3f + (float) 0, offsetZ + 1f, 0.0f, 1.0f, zNormal),
                        new Vertex(-1.9f + offsetX, -3f + (float) 0, offsetZ + 1f, 1f, 1.0f, zNormal),

                        // back
                        new Vertex(-2f + offsetX, -1.1f + (float) 0, offsetZ + 0.9f, 0.0f, 0.0f, mzNormal),
                        new Vertex(-1.9f + offsetX, -1.1f + (float) 0, offsetZ + 0.9f, 1.0f, 0.0f, mzNormal),
                        new Vertex(-2f + offsetX, -3f + (float) 0, offsetZ + 0.9f, 0.0f, 1.0f, mzNormal),
                        new Vertex(-1.9f + offsetX, -3f + (float) 0, offsetZ + 0.9f, 1f, 1.0f, mzNormal),

                        // left
                        new Vertex(-2f + offsetX, -1.1f + (float) 0, offsetZ + 1f, 0.0f, 0.0f, mxNormal),
                        new Vertex(-2f + offsetX, -3f + (float) 0, offsetZ + 1f, 1.0f, 0.0f, mxNormal),
                        new Vertex(-2f + offsetX, -1.1f + (float) 0, offsetZ + 0.9f, 0.0f, 1.0f, mxNormal),
                        new Vertex(-2f + offsetX, -3f + (float) 0, offsetZ + 0.9f, 1f, 1.0f, mxNormal),

                        // right
                        new Vertex(-1.9f + offsetX, -1.1f + (float) 0, offsetZ + 1f, 0.0f, 0.0f, xNormal),
                        new Vertex(-1.9f + offsetX, -3f + (float) 0, offsetZ + 1f, 1.0f, 0.0f, xNormal),
                        new Vertex(-1.9f + offsetX, -1.1f + (float) 0, offsetZ + 0.9f, 0.0f, 1.0f, xNormal),
                        new Vertex(-1.9f + offsetX, -3f + (float) 0, offsetZ + 0.9f, 1f, 1.0f, xNormal),
                });
    }

    private ModelObject[] get2DObjects() {
        Texture sky = new Texture(context, R.drawable.sky);
        Texture red = new Texture(context, R.drawable.red);
        Texture blue = new Texture(context, R.drawable.blue);
        Texture white = new Texture(context, R.drawable.white);

        int vertexShaderRawId = R.raw.vertex_shader;
        int fragmentShaderRawId = R.raw.fragment_shader;

        float[] zNormal = new float[]{0f, 0, 1f};

        ModelObject point = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_LINE_LOOP, red,
                new Vertex[]{

                });

        ModelObject skyPlane = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLE_STRIP, sky,
                new Vertex[]{
                        new Vertex(-1, 1, 0, 0, 0, zNormal),
                        new Vertex(-1, -1, 0, 0, 1, zNormal),
                        new Vertex(1, -1, 0, 1, 1, zNormal),
                        new Vertex(1, 1, 0, 1, 0, zNormal),
                        new Vertex(-1, 1, 0, 0, 0, zNormal),
                });

        ModelObject planer0 = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLES, white,
                new Vertex[]{
                        new Vertex(-0.7f, 0.3f, 0f, 0, 0, zNormal),
                        new Vertex(-0.55f, 0.2f, 0f, 0, 1, zNormal),
                        new Vertex(-0.25f, 0.2f, 0f, 1, 1, zNormal),
                });

        ModelObject planer1 = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLES, blue,
                new Vertex[]{
                        new Vertex(-0.7f, 0.3f, 0f, 0, 0, zNormal),
                        new Vertex(-0.25f, 0.2f, 0f, 0, 1, zNormal),
                        new Vertex(0.35f, 0.35f, 0f, 1, 1, zNormal),
                });

        ModelObject planer2 = new ModelObject(ProgramUtils.createProgram(context, vertexShaderRawId, fragmentShaderRawId),
                GL_TRIANGLES, red,
                new Vertex[]{
                        new Vertex(-0.25f, 0.2f, 0f, 0, 0, zNormal),
                        new Vertex(0.15f, -0.15f, 0f, 0, 1, zNormal),
                        new Vertex(0.15f, 0.2f, 0f, 1, 1, zNormal),

                        new Vertex(-0.25f, 0.2f, 0f, 0, 0, zNormal),
                        new Vertex(0.45f, 0.2f, 0f, 0, 1, zNormal),
                        new Vertex(0.55f, 0.4f, 0f, 1, 1, zNormal),
                });


        return new ModelObject[]{point, planer0, planer1, planer2, skyPlane};
    }
}
