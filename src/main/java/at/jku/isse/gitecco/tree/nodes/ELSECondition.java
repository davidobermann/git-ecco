package at.jku.isse.gitecco.tree.nodes;


import at.jku.isse.gitecco.tree.visitor.TreeVisitor;
import at.jku.isse.gitecco.tree.visitor.Visitable;

/**
 * Class for representing an ELSECondition.
 * The name is slightly misleading since this has no condition on its own.
 * This feature/cond. depends on the corresponding IF/IFDEF/IFNDEF Condition.
 * It should represent an ELSE clause of an IF ELSE PPStatement.
 */
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
