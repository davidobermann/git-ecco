package at.jku.isse.gitecco.tree;


public abstract class FileNode extends Node {
    private final String name;

    public FileNode(Node parent, String name) {
        super(parent);
        this.name= name;
    }

    public String getFilePath() {
        return this.name;
    }
}
