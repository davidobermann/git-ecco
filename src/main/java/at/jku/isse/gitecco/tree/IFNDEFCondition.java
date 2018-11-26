package at.jku.isse.gitecco.tree;

public final class IFNDEFCondition extends ConditionalNode {
    private final String condition;

    public IFNDEFCondition(Node parent, String condition) {
        super(parent);
        this.condition = condition;
    }

    public String getCondition() {
        return this.condition;
    }
}
