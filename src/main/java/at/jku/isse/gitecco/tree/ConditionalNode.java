package at.jku.isse.gitecco.tree;

import at.jku.isse.gitecco.conditionparser.ConditionParser;

import java.util.ArrayList;
import java.util.List;

public class ConditionalNode extends ConditionNode {
    final String condition;
    final List<ConditionBlockNode> children;

    public ConditionalNode(Node parent, int lineFrom, int lineTo, String condition) {
        super(parent, lineFrom, lineTo);
        this.condition = condition;
        children = new ArrayList<ConditionBlockNode>();
    }

    public String[] getFeatureNames() {
        return ConditionParser.parseCondition(condition);
    }

    public String getCondition() {
        return condition;
    }

    public void addChild(ConditionBlockNode n) {
        this.children.add(n);
    }
}
