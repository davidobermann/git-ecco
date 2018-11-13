package at.jku.isse.gitecco.tree;


public abstract class FileNode extends Node {
    final String filePath;

    public FileNode(Node parent, String filePath) {
        super(parent);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }
}
