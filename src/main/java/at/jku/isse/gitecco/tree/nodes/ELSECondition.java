package at.jku.isse.gitecco.tree.nodes;


import at.jku.isse.gitecco.tree.visitor.TreeVisitor;
import at.jku.isse.gitecco.tree.visitor.Visitable;

public final class ELSECondition extends ConditionalNode implements Visitable {
    public ELSECondition(Node parent) {
        super(parent);
    }

    @Override
    public String getCondition() {
        return "";
    }

    @Override
    public void accept(TreeVisitor v) {
        for (ConditionBlockNode child : getChildren()) {
            child.accept(v);
        }
        v.visit(this);
    }

}
