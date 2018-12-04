package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.TreeVisitor;

public final class BinaryFileNode extends FileNode {

    public BinaryFileNode(Node parent, String filePath) {
        super(parent, filePath);
    }

    @Override
    public void accept(TreeVisitor v) {
        v.visit(this);
    }
}
