package at.jku.isse.gitecco.tree;

public final class IFCondition extends ConditionalNode {
    public IFCondition(Node parent, int lineFrom, int lineTo, String condition) {
        super(parent, lineFrom, lineTo, condition);
    }
}
