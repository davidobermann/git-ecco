package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public final class ConditionBlockNode extends ConditionNode {
    private final List<ConditionalNode> elseIfBlocks;
    private ConditionalNode ifBlock;
    private ELSECondition elseBlock;

    public ConditionBlockNode(Node parent, int lineFrom, int lineTo) {
        super(parent, lineFrom, lineTo);
        this.elseIfBlocks = new ArrayList<>();
    }

    public String[] getContainedFeatures() {
        //TODO: traverse subtree under this node and collect all feature names.
        return null;
    }

    public void setIfBlock(ConditionalNode n) {
        this.ifBlock = n;
    }

    public void addElseIfBlock(ConditionalNode n) {
        this.elseIfBlocks.add(n);
    }

    public void setElseBlock(ELSECondition n) {
        this.elseBlock = n;
    }

}
