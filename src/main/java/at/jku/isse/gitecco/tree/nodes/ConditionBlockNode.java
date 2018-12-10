package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.TreeVisitor;
import at.jku.isse.gitecco.tree.visitor.Visitable;

import java.util.ArrayList;
import java.util.List;

public final class ConditionBlockNode extends ConditionNode implements Visitable {
    private final List<IFCondition> elseIfBlocks;
    private ConditionalNode ifBlock;
    private ELSECondition elseBlock;

    public ConditionBlockNode(Node parent) {
        super(parent);
        this.elseIfBlocks = new ArrayList<>();
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

    @Override
    public void accept(TreeVisitor v) {
        if(elseBlock != null) elseBlock.accept(v);
        for (IFCondition elseIfBlock : elseIfBlocks) {
            elseIfBlock.accept(v);
        }
        if(ifBlock != null) ifBlock.accept(v);
        v.visit(this);
    }
}
