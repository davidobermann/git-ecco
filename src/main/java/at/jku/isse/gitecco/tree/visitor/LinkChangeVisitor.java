package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.git.Change;
import at.jku.isse.gitecco.tree.nodes.*;

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
        if(c.containsChange(change) || change.contains(c)) c.setChanged();
    }

    @Override
    public void visit(IFDEFCondition c) {
        if(c.containsChange(change) || change.contains(c)) c.setChanged();
    }

    @Override
    public void visit(IFNDEFCondition c) {
        if(c.containsChange(change) || change.contains(c)) c.setChanged();
    }

    @Override
    public void visit(ELSECondition c) {
        if(c.containsChange(change) || change.contains(c)) c.setChanged();
    }

}
