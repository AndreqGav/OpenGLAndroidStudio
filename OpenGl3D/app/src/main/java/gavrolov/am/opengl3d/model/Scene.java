package gavrolov.am.opengl3d.model;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class Scene {
    public ModelObject[] modelObjects;

    protected float xCamera, yCamera, zCamera;
    protected float xLightPosition, yLightPosition, zLightPosition;

    private int aPositionLocation;
    private int aTextureLocation;
    private int aNormalLocation;
    private int aColorLocation;
    private int uMatrixLocation;
    private int uTextureUnitLocation;
    private int uLightPositionLocation;
    private int uCameraLocation;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer colorBuffer;

    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private final static int VERTEX_COUNT = 4;
    private final static int TEXTURE_COUNT = 2;
    private final static int NORMAL_COUNT = 3;
    private final static int COLOR_COUNT = 4;

    public int CAMERA_TYPE = 2; // 1 - perspectiveM, 2 - frustumM, 3 - orthoM

    public Scene() {
    }

    private void getLocations(ModelObject modelObject) {
        int programId = modelObject.programId;
        glUseProgram(programId);

        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        aNormalLocation = glGetAttribLocation(programId, "a_Normal");
        aColorLocation = glGetAttribLocation(programId, "a_Color");
        aTextureLocation = glGetAttribLocation(programId, "a_Texture");

        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit");
        uCameraLocation = glGetUniformLocation(programId, "u_Camera");
        uLightPositionLocation = glGetUniformLocation(programId, "u_LightPosition");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createBuffers() {
        // Вершины.
        float[] vertices = toFloat(Arrays.stream(modelObjects)
                .flatMapToDouble(s -> Arrays.stream(s.vertices)
                        .flatMapToDouble(ss -> Arrays.stream(new double[]{(double) ss.x, (double) ss.y, (double) ss.z, (double) ss.w})))
                .toArray());
        vertexBuffer = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices);

        // Нормали.
        float[] normals = toFloat(Arrays.stream(modelObjects)
                .flatMapToDouble(s -> Arrays.stream(s.vertices)
                        .flatMapToDouble(ss -> Arrays.stream(new double[]{(double) ss.normalX, (double) ss.normalY, (double) ss.normalZ})))
                .toArray());
        normalBuffer = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        normalBuffer.put(normals);

        // Цвета.
        float[] colors = toFloat(Arrays.stream(modelObjects)
                .flatMapToDouble(s -> Arrays.stream(s.vertices)
                        .flatMapToDouble(ss -> Arrays.stream(new double[]{(double) ss.colorR, (double) ss.colorG, (double) ss.colorB, (double) ss.colorA})))
                .toArray());
        colorBuffer = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        colorBuffer.put(colors);

        // Текстуры.
        float[] texture = toFloat(Arrays.stream(modelObjects)
                .flatMapToDouble(s -> Arrays.stream(s.vertices)
                        .flatMapToDouble(ss -> Arrays.stream(new double[]{(double) ss.textureX, (double) ss.textureY})))
                .toArray());
        textureBuffer = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(texture);
    }

    public void bindBuffers() {
        if (modelObjects == null) {
            return;
        }

        for (ModelObject modelObject : modelObjects) {
            bindBuffers(modelObject);
        }
    }

    public void bindBuffers(ModelObject modelObject) {
        getLocations(modelObject);

        // Вершины.
        vertexBuffer.position(0);
        glVertexAttribPointer(aPositionLocation, VERTEX_COUNT, GL_FLOAT, false, 0, vertexBuffer);
        glEnableVertexAttribArray(aPositionLocation);

        // Нормали.
        normalBuffer.position(0);
        glVertexAttribPointer(aNormalLocation, NORMAL_COUNT, GL_FLOAT, false, 0, normalBuffer);
        glEnableVertexAttribArray(aNormalLocation);

        // Цвета.
        colorBuffer.position(0);
        glVertexAttribPointer(aColorLocation, COLOR_COUNT, GL_FLOAT, false, 0, colorBuffer);
        glEnableVertexAttribArray(aColorLocation);

        // Текстуры.
        textureBuffer.position(0);
        glVertexAttribPointer(aTextureLocation, TEXTURE_COUNT, GL_FLOAT, false, 0, textureBuffer);
        glEnableVertexAttribArray(aTextureLocation);
    }

    public void drawObjects() {
        if (modelObjects == null) {
            return;
        }

        int offset = 0;
        for (ModelObject modelObject : modelObjects) {
            getLocations(modelObject);

            bindCamera(modelObject);
            bindLight(modelObject);
            bindObjectTexture(modelObject);
            bindObjectMatrix(modelObject);
            glDrawArrays(modelObject.drawType, offset, modelObject.vertices.length);
            offset += modelObject.vertices.length;
        }
    }

    public void updateView(int width, int height) {
        updateProjectionMatrix(width, height);
    }

    public void setCameraPosition(float cameraX, float cameraY, float cameraZ, float centerX, float centerY, float centerZ) {
        float upX = 0;
        float upY = 1;
        float upZ = 0;

        xCamera = cameraX;
        yCamera = cameraY;
        zCamera = cameraZ;

        Matrix.setLookAtM(viewMatrix, 0, cameraX, cameraY, cameraZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void setLightPosition(float x, float y, float z) {
        xLightPosition = x;
        yLightPosition = y;
        zLightPosition = z;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    private void bindObjectTexture(ModelObject modelObject) {
        Texture texture = modelObject.texture;

        if (texture != null) {

            //выбираем текущий текстурный блок GL_TEXTURE0
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            //в текстурном блоке GL_TEXTURE0
            //делаем активной текстуру с именем texture0.getName()
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getName());

            //выполняем связь между объектом  texture0 и униформой u_texture0
            //в нулевом текстурном блоке
            GLES20.glUniform1i(uTextureUnitLocation, 0);
        }
    }

    private void bindObjectMatrix(ModelObject modelObject) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, modelViewProjectionMatrix, 0);
    }

    private void bindCamera(ModelObject modelObject) {
        glUniform3f(uCameraLocation, xCamera, yCamera, zCamera);
    }

    private void bindLight(ModelObject modelObject) {
        glUniform3f(uLightPositionLocation, xLightPosition, yLightPosition, zLightPosition);
    }

    private void updateProjectionMatrix(int width, int height) {
        float ratio;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 2;
        float far = 16;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        if (CAMERA_TYPE == 1) {
            float value = (float) (width) / height;
            Matrix.perspectiveM(projectionMatrix, 0, 90, value, 0.1f, 100);
        } else if (CAMERA_TYPE == 2) {
            Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
        } else if (CAMERA_TYPE == 3) {
            Matrix.orthoM(projectionMatrix, 0, left, right, bottom, top, near, far);
        }
    }

    private float[] toFloat(double[] arr) {
        float[] floatArray = new float[arr.length];
        for (int i = 0; i < arr.length; i++) {
            floatArray[i] = (float) arr[i];
        }

        return floatArray;
    }
}
