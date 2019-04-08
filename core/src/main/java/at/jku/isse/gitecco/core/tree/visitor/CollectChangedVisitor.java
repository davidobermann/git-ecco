package at.jku.isse.gitecco.core.tree.visitor;

import at.jku.isse.gitecco.core.tree.nodes.*;

import java.util.HashSet;
import java.util.Set;

public class CollectChangedVisitor implements TreeVisitor {
    private final Set<ConditionalNode> changed;

    public CollectChangedVisitor() {
        changed = new HashSet<>();
    }

    @Override
    public void visit(RootNode n) {

    }

    @Override
    public void visit(BinaryFileNode n) {

    }

    @Override
    public void visit(SourceFileNode n) {

    }

    @Override
    public void visit(ConditionBlockNode n) {

    }

    @Override
    public void visit(IFCondition c) {
        if(c.isChanged()) {
            changed.add(c);
        }
    }

    @Override
    public void visit(IFDEFCondition c) {
        if(c.isChanged()) {
            changed.add(c);
        }
    }

    @Override
    public void visit(IFNDEFCondition c) {
        if(c.isChanged()) {
            changed.add(c);
        }
    }

    @Override
    public void visit(ELSECondition c) {
        if(c.isChanged()) {
            changed.add(c);
        }
    }
}
