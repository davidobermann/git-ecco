package at.jku.isse.gitecco.tree;

import at.jku.isse.gitecco.conditionparser.ConditionParser;

import java.util.ArrayList;
import java.util.List;

public class IFCondition extends ConditionNode {
    private final List<ConditionNode> children;
    final String condition;

    public IFCondition(Node parent, String condition, int lineFrom, int lineTo) {
        super(parent, lineFrom, lineTo);
        this.condition = condition;
        this.children = new ArrayList<ConditionNode>();
    }

    public String[] getFeatures() {
        return ConditionParser.parseCondition(this.condition);
    }

    public String getCondition() {
        return condition;
    }
}
