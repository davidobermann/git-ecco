package at.jku.isse.gitecco.tree;

import at.jku.isse.gitecco.conditionparser.ConditionParser;

import java.util.ArrayList;
import java.util.List;

public class ELSEIFCondition extends IFCondition {
    private final List<ConditionNode> children;

    public ELSEIFCondition(Node parent, String condition, int lineFrom, int lineTo) {
        super(parent, condition, lineFrom, lineTo);
        this.children = new ArrayList<ConditionNode>();
    }

    public String[] getFeatures() {
        return ConditionParser.parseCondition(this.condition);
    }

    public String getCondition() {
        return condition;
    }
}
