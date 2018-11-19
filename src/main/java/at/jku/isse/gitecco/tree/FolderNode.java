package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public final class FolderNode extends FileNode {
    private final List<FileNode> children;

    public FolderNode(Node parent, String path) {
        super(parent, path);
        this.children = new ArrayList<FileNode>();
    }

    public void addChild(FileNode n) {
        children.add(n);
    }
}
