package at.jku.isse.gitecco.tree;

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
}
