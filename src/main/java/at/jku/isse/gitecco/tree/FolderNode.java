package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public class FolderNode extends FileNode{
    private final List<FileNode> children;

    public FolderNode(Node parent, String name, String path) {
        super(parent, name, path);
        this.children = new ArrayList<FileNode>();
    }
}
