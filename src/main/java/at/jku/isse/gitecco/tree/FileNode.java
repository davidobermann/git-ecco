package at.jku.isse.gitecco.tree;

public abstract class FileNode extends Node {
    final String name;
    final String path;

    public FileNode(Node parent, String name, String path) {
        super(parent);
        this.name = name;
        this.path = path;
    }
}
