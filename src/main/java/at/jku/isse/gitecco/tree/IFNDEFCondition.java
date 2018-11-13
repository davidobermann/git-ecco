package at.jku.isse.gitecco.tree;


import at.jku.isse.gitecco.conditionparser.ConditionParser;

import java.util.ArrayList;
import java.util.List;

public class IFNDEFCondition extends ConditionNode {
    private final String definition;
    private final List<ConditionNode> children;

    public IFNDEFCondition(Node parent, String definition, int lineFrom, int lineTo) {
        super(parent, lineFrom, lineTo);
        this.definition = definition;
        this.children = new ArrayList<ConditionNode>();
    }

    public String getDefinition() {
        return definition;
    }

    public String[] getFeatureNames() {
        return ConditionParser.parseCondition(definition);
    }
}
