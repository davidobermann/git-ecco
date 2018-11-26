package at.jku.isse.gitecco.tree;

public final class IFCondition extends ConditionalNode {
    private final String condition;

    public IFCondition(Node parent, String condition) {
        super(parent);
        this.condition = condition;
    }

    public String getCondition() {
        return this.condition;
    }
}
