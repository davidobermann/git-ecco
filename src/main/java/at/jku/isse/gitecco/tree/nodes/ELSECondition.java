package at.jku.isse.gitecco.tree.nodes;


import at.jku.isse.gitecco.tree.visitor.TreeVisitor;

public final class ELSECondition extends ConditionalNode {
    public ELSECondition(Node parent) {
        super(parent);
    }

    @Override
    public void accept(TreeVisitor v) {
        for (ConditionBlockNode child : getChildren()) {
            v.visit(this);
            child.accept(v);
        }
    }

}
