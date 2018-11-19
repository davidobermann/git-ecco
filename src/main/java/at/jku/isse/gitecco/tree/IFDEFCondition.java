package at.jku.isse.gitecco.tree;

public final class IFDEFCondition extends ConditionalNode {
    public IFDEFCondition(Node parent, int lineFrom, int lineTo, String condition) {
        super(parent, lineFrom, lineTo, condition);
    }
}
