package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.tree.nodes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetAllChangedConditionsVisitor implements TreeVisitor {

    private final List<String> conditions;

    public GetAllChangedConditionsVisitor() {
        this.conditions = new ArrayList<>();
    }

    public Collection<String> getAllConditions() {
        return conditions;
    }

    public String getAllConditionsConjuctive() {
        String ret = "";

        for (String c : conditions) {
            ret += " & " + c;
        }

        return ret.substring(2);
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
        }
    }

    @Override
    public void visit(IFDEFCondition c) {
        if(c.isChanged()) {
            String cond = c.getCondition();
            cond = cond.replace('!','~').replace("&&","&").replace("||","|");
            conditions.add(cond);
        }
    }

    @Override
    public void visit(IFNDEFCondition c) {
        if(c.isChanged()) {
            String cond = c.getCondition();
            cond = cond.replace('!','~').replace("&&","&").replace("||","|");
            conditions.add(cond);
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
        }
    }
}
