package at.jku.isse.gitecco.tree.nodes;

/**
 * Abstract class for FileNodes --> will be used for BinaryFileNodes and SourceFileNodes.
 */
public abstract class FileNode extends Node{
    private final String pathName;

    public FileNode(Node parent, String name) {
        super(parent);
        this.pathName= name;
    }

    /**
     * Returns the path of the file which is represented by this node.
     * @return
     */
    public String getFilePath() {
        return this.pathName;
    }
}
