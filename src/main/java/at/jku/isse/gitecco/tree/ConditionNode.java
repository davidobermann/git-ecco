package at.jku.isse.gitecco.tree;

public abstract class ConditionNode extends Node {
    private final int lineFrom;
    private final int lineTo;

    public ConditionNode(Node parent, int lineFrom, int lineTo) {
        super(parent);
        this.lineFrom = lineFrom;
        this.lineTo = lineTo;
    }

    public int getLineFrom() {
        return lineFrom;
    }

    public int getLineTo() {
        return lineTo;
    }

    public SourceFileNode getContainingSourceFile() {
        Node temp = this;
        while (!(temp instanceof SourceFileNode) && temp.getParent() != null) {
            temp = temp.getParent();
        }
        return (SourceFileNode) temp;
    }
}
