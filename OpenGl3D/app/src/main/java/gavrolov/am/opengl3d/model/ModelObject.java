package gavrolov.am.opengl3d.model;

public class ModelObject extends SceneObject {
    public Vertex[] vertices;
    public int drawType;
    public Texture texture;
    public int programId = 0;

    public ModelObject(int drawType, Vertex[] vertices) {
        this.drawType = drawType;
        this.vertices = vertices;
    }

    public ModelObject(int programId, int drawType, Texture texture, Vertex[] vertices) {
        this(drawType, vertices);
        this.texture = texture;
        this.programId = programId;
    }
}
