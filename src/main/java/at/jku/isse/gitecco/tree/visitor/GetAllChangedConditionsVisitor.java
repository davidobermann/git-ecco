package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.tree.nodes.*;

import java.util.*;

/**
 * Visitor for retrieving all changed BLocks/Conditions/Features.
 */
public class GetAllChangedConditionsVisitor implements TreeVisitor {

    private final Set<String> conditions;
    private final Set<String> affected;

    public GetAllChangedConditionsVisitor() {
        this.conditions = new HashSet<>();
        this.affected = new HashSet<>();
    }

    public Collection<String> getAllChangedConditions() {
        return conditions;
    }

    public Collection<String> getAllAffectedConditions() {
        return affected;
    }

    public String getAllConditionsConjuctive() {
        String ret = "";

        for (String c : conditions) {
            ret += " & " + c;
        }

        return ret.substring(2);
    }

    private void addAffected(Set<String> set, Node n) {
        //TODO: create algorithm to collect all affected (all parent nodes above this changed node)
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
            String cond = c.getCondition();
            cond = cond.replace('!','~').replace("&&","&").replace("||","|");
            conditions.add(cond);
            //TODO: call add Affected on parent
        }
    }

    @Override
    public void visit(IFDEFCondition c) {
        if(c.isChanged()) {
            String cond = c.getCondition();
            cond = cond.replace('!','~').replace("&&","&").replace("||","|");
            conditions.add(cond);
            //TODO: call add Affected on parent
        }
    }

    @Override
    public void visit(IFNDEFCondition c) {
        if(c.isChanged()) {
            String cond = c.getCondition();
            cond = cond.replace('!','~').replace("&&","&").replace("||","|");
            conditions.add(cond);
            //TODO: call add Affected on parent
        }
    }

    @Override
    public void visit(ELSECondition c) {
        if(c.isChanged()) {
            String cond = "";
            ConditionBlockNode bn = (ConditionBlockNode)c.getParent();
            cond += "~" + bn.getIfBlock().getCondition();

            for (IFCondition ifCond : bn.getElseIfBlocks()) {
                cond += "~" + ifCond.getCondition();
            }

            cond = cond.replace('!','~').replace("&&","&").replace("||","|");

            conditions.add(cond);
            //TODO: call add Affected on parent
        }
    }
}
