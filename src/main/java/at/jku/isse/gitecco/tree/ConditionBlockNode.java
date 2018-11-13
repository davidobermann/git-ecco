package at.jku.isse.gitecco.tree;

import java.util.List;

public final class ConditionBlockNode extends ConditionNode{
    private ConditionNode ifBlock;
    private List<ConditionNode> elseIfBlock;
    private ConditionNode elseBlock;

    public ConditionBlockNode(Node parent, int lineFrom, int lineTo) {
        super(parent, lineFrom, lineTo);
    }

    public void addElseIfBlock(ConditionNode node) {
        this.elseIfBlock.add(node);
    }

    public String[] getContainedFeatures() {
        //TODO: traverse subtree under this nde and collect all feature names.
        return null;
    }

}
