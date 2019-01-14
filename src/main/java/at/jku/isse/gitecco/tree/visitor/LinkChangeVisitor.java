package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.git.Change;
import at.jku.isse.gitecco.tree.nodes.*;

/**
 * Visitor for linking a change passed by constructor or set by the setter
 * to the nodes affected by it.
 */
public class LinkChangeVisitor implements TreeVisitor{
    private Change change;

    public LinkChangeVisitor(Change change) {
        this.change = change;
    }

    public LinkChangeVisitor() {

    }

    public void setChange(Change c) {
        this.change = c;
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
        if(change != null && (c.containsChange(change) || change.contains(c))) {
            c.setChanged();
            //this is necessary to mark newly added features as changed.
            if(!change.contains(c)) change = null;
        }
    }

    @Override
    public void visit(IFDEFCondition c) {
        if(change != null && (c.containsChange(change) || change.contains(c))) {
            c.setChanged();
            if(!change.contains(c)) change = null;
        }
    }

    @Override
    public void visit(IFNDEFCondition c) {
        if(change != null && (c.containsChange(change) || change.contains(c))) {
            c.setChanged();
            if(!change.contains(c)) change = null;
        }
    }

    @Override
    public void visit(ELSECondition c) {
        if(change != null && (c.containsChange(change) || change.contains(c))) {
            c.setChanged();
            if(!change.contains(c)) change = null;
        }
    }

}
