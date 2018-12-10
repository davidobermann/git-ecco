package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.TreeVisitor;
import at.jku.isse.gitecco.tree.visitor.Visitable;

import java.util.ArrayList;
import java.util.List;

public final class RootNode extends Node implements Visitable {
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

    @Override
    public void accept(TreeVisitor v) {
        for (FileNode child : children) {
            v.visit(this);
            child.accept(v);
        }
    }
}
