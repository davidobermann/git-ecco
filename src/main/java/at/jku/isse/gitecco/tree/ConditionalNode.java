package at.jku.isse.gitecco.tree;

import at.jku.isse.gitecco.conditionparser.ConditionParser;

import java.util.ArrayList;
import java.util.List;

public abstract class ConditionalNode extends ConditionNode {
    private final List<ConditionBlockNode> children;
    private String condition;

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

    public void setCondition(String cond) {
        if (condition == null) this.condition = cond;
    }

    public void addChild(ConditionBlockNode n) {
        this.children.add(n);
    }
}
