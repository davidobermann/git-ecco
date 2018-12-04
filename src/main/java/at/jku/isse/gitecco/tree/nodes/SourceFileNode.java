package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.TreeVisitor;

public final class SourceFileNode extends FileNode {
    private ConditionBlockNode base;

    public SourceFileNode(Node parent, String filePath) {
        super(parent, filePath);
        base = null;
    }

    public void setBase(ConditionBlockNode n) throws IllegalAccessException {
        if(base == null) this.base = n;
        else throw new IllegalAccessException("Cannot set base twice.");
    }

    @Override
    public void accept(TreeVisitor v) {
        base.accept(v);
        v.visit(this);
    }
}
