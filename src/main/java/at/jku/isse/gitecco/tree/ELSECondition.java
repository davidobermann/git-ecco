package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public class ELSECondition extends ConditionNode {
    private final List<ConditionNode> children;

    public ELSECondition(Node parent, String condition, int lineFrom, int lineTo) {
        super(parent, lineFrom, lineTo);
        this.children = new ArrayList<ConditionNode>();
    }
}
