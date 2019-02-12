package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.tree.nodes.*;

/**
 * Visitor for marking blocks with changed features also as changed.
 */
public class ValidateChangeVisitor implements TreeVisitor {
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
    /**
     *
     * Probably not very useful but worth keeping it for now.
     *
     * The important part:
     * If any of the block's children changed --> the block changed.
     */
    public void visit(ConditionBlockNode n) {
        if((n.getElseBlock() != null && n.getElseBlock().isChanged()) || n.getIfBlock().isChanged()) {
            n.setChanged();
            return;
        }
        for (IFCondition elseIfBlock : n.getElseIfBlocks()) {
            if(elseIfBlock.isChanged()) {
                n.setChanged();
                return;
            }
        }
    }

    @Override
    public void visit(IFCondition c) {

    }

    @Override
    public void visit(IFDEFCondition c) {

    }

    @Override
    public void visit(IFNDEFCondition c) {

    }

    @Override
    /**
     * If an ELSE block is marked as changed for the config the first positive condition is needed.
     * This marks the nearest condition as changed. If it is an ELSE block again the bottom up traverse
     * will change it next, so this is kind of recursive bottom up.
     */
    public void visit(ELSECondition c) {
        //TODO: Positive conditions wanted --> if nearest IF Block is ~A for example the next one is wanted.
        //solved with other technique of performing the ecco commits.
        if(c.isChanged()) {
            c.getParent().getParent().setChanged();
            //c.getParent().getParent().getCondition() --> checken ob negativ.
        }
    }
}
