package gavrolov.am.opengl3d.utils;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glLinkProgram;

import android.content.Context;

public class ProgramUtils {
    public static int createProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = glCreateProgram();
        if (programId == 0) {
            return 0;
        }
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }
        return programId;
    }

    public static int createProgram(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        final int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, vertexShaderRawId);
        final int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, fragmentShaderRawId);
        return createProgram(vertexShaderId, fragmentShaderId);
    }
}
