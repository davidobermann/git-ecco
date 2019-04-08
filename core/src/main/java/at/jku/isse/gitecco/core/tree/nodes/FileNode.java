package at.jku.isse.gitecco.core.tree.nodes;

/**
 * Abstract class for FileNodes --> will be used for BinaryFileNodes and SourceFileNodes.
 */
public abstract class FileNode extends Node{
    private final String pathName;
    private final RootNode parent;

    public FileNode(RootNode parent, String name) {
        this.parent = parent;
        this.pathName= name;
    }

    /**
     * Returns the path of the file which is represented by this node.
     * @return
     */
    public String getFilePath() {
        return this.pathName;
    }

    @Override
    public RootNode getParent() {
        return this.parent;
    }
}
