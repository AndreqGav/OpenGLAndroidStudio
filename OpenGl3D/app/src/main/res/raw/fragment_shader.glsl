precision mediump float;

uniform sampler2D u_TextureUnit;
uniform vec3 u_Camera;
uniform vec3 u_LightPosition;

varying vec4 v_Color;
varying vec2 v_Texture;
varying vec3 v_Vertex;
varying vec3 v_Normal;

void main()
{
    vec3 n_normal = normalize(v_Normal);
    vec3 lightvector = normalize(u_LightPosition - v_Vertex);
    vec3 lookvector = normalize(u_Camera - v_Vertex);
    float ambient = 0.4;
    float k_diffuse = 0.5;
    float k_specular = 0.4;
    float diffuse = k_diffuse * max(dot(n_normal, lightvector), 0.0);
    vec3 reflectvector = reflect(-lightvector, n_normal);
    float specular = k_specular * pow(max(dot(lookvector, reflectvector), 0.0), 40.0);
    vec4 one = vec4(1.0, 1.0, 1.0, 1.0);
    vec4 lightColor = (ambient+diffuse+specular) * one;
    gl_FragColor = texture2D(u_TextureUnit, v_Texture) * lightColor;
}