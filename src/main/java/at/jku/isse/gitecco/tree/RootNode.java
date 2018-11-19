package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public final class RootNode extends Node {
    private final String path;
    private final List<FileNode> children;

    public RootNode(String path) {
        super(null);
        this.path = path;
        this.children = new ArrayList<>();
    }

    public void addChild(FileNode n) {
        children.add(n);
    }

}
