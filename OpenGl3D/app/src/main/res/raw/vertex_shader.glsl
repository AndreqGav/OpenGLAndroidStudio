attribute vec3 a_Position;
attribute vec4 a_Color;
attribute vec2 a_Texture;
attribute vec3 a_Normal;

uniform mat4 u_Matrix;

varying vec4 v_Color;
varying vec2 v_Texture;
varying vec3 v_Vertex;
varying vec3 v_Normal;

varying lowp vec3 frag_Position;

void main()
{
    v_Vertex = a_Position;
    vec3 n_normal = normalize(a_Normal);
    v_Normal = n_normal;

    v_Color = a_Color;
    v_Texture = a_Texture;

    gl_PointSize = 25.0;
    gl_Position = u_Matrix * vec4(a_Position, 1.0);
}