package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public final class ELSECondition extends ConditionNode {
    private final List<ConditionBlockNode> children;

    public ELSECondition(Node parent, String condition, int lineFrom, int lineTo) {
        super(parent, lineFrom, lineTo);
        this.children = new ArrayList<ConditionBlockNode>();
    }

    public void addChild(ConditionBlockNode n) {
        children.add(n);
    }

}
