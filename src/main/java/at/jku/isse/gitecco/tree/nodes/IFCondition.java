package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.TreeVisitor;

public final class IFCondition extends ConditionalNode {
    private final String condition;

    public IFCondition(Node parent, String condition) {
        super(parent);
        this.condition = condition;
    }

    public String getCondition() {
        return this.condition;
    }

    @Override
    public void accept(TreeVisitor v) {
        for (ConditionBlockNode child : getChildren()) {
            child.accept(v);
            v.visit(this);
        }
    }

}
