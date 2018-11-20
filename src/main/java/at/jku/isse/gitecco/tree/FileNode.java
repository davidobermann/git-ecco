package at.jku.isse.gitecco.tree;


public abstract class FileNode extends Node {
    private final String pathName;

    public FileNode(Node parent, String name) {
        super(parent);
        this.pathName= name;
    }

    public String getFilePath() {
        return this.pathName;
    }
}
