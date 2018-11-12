package at.jku.isse.gitecco.tree;

public abstract class ConditionNode extends Node {
    final int lineFrom;
    final int lineTo;

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
}
