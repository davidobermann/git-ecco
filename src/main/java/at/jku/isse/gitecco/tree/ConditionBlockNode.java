package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public final class ConditionBlockNode extends ConditionNode {
    private final List<IFCondition> elseIfBlocks;
    private ConditionalNode ifBlock;
    private ELSECondition elseBlock;

    public ConditionBlockNode(Node parent) {
        super(parent);
        this.elseIfBlocks = new ArrayList<>();
    }

    public String[] getContainedFeatures() {
        //TODO: traverse subtree under this node and collect all feature names.
        return null;
    }

    public ConditionalNode setIfBlock(ConditionalNode n) {
        this.ifBlock = n;
        return n;
    }

    public IFCondition addElseIfBlock(IFCondition n) {
        this.elseIfBlocks.add(n);
        return n;
    }

    public ELSECondition setElseBlock(ELSECondition n) {
        this.elseBlock = n;
        return n;
    }

    public ConditionalNode getIfBlock() {
        return this.ifBlock;
    }

    public ELSECondition getElseBlock() {
        return this.elseBlock;
    }

    public List<IFCondition> getElseIfBlocks() {
        return elseIfBlocks;
    }
}
