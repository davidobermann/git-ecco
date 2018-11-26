package at.jku.isse.gitecco.tree;

public final class IFDEFCondition extends ConditionalNode {
    private final String condition;

    public IFDEFCondition(Node parent, String condition) {
        super(parent);
        this.condition = condition;
    }

    public String getCondition() {
        return this.condition;
    }
}
