package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.tree.nodes.*;

import java.util.*;

/**
 * Visitor for retrieving all changed BLocks/Conditions/Features.
 */
public class GetAllChangedConditionsVisitor implements TreeVisitor {
    /**Set of all the changed conditions*/
    private final Set<String> conditions;
    /**Set of all conditions affected by changes*/
    private final Set<String> affected;

    public GetAllChangedConditionsVisitor() {
        this.conditions = new HashSet<>();
        this.affected = new HashSet<>();
    }

    public Collection<String> getAllChangedConditions() {
        return Collections.unmodifiableSet(conditions);
    }

    public Collection<String> getAllAffectedConditions() {
        return Collections.unmodifiableSet(affected);
    }

    public String getAllConditionsConjuctive() {
        String ret = "";

        //all changed conditions
        for (String c : conditions) ret += " & " + c;
        //all affected conditions
        for (String s : affected) ret += " & " + s;

        return ret.length() > 2 ? ret.substring(2) : ret;
    }

    private void addAffected(ConditionalNode n) {
        affected.add(n.getCondition());
        if(n.getParent() instanceof ConditionBlockNode) addAffected(n.getParent());
    }

    private void addAffected(ConditionBlockNode n) {
        if(n.getParent() instanceof ConditionalNode) addAffected(n.getParent());
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
            addAffected(c.getParent());
        }
    }

    @Override
    public void visit(IFDEFCondition c) {
        if(c.isChanged()) {
            String cond = c.getCondition();
            cond = cond.replace('!','~').replace("&&","&").replace("||","|");
            conditions.add(cond);
            addAffected(c.getParent());
        }
    }

    @Override
    public void visit(IFNDEFCondition c) {
        if(c.isChanged()) {
            String cond = c.getCondition();
            cond = cond.replace('!','~').replace("&&","&").replace("||","|");
            conditions.add(cond);
            addAffected(c.getParent());
        }
    }

    @Override
    public void visit(ELSECondition c) {
        if(c.isChanged()) {
            String cond = "";
            ConditionBlockNode bn = c.getParent();
            cond += "~" + bn.getIfBlock().getCondition();

            for (IFCondition ifCond : bn.getElseIfBlocks()) {
                cond += "~" + ifCond.getCondition();
            }

            cond = cond.replace('!','~').replace("&&","&").replace("||","|");

            conditions.add(cond);
            addAffected(c.getParent());
        }
    }
}
