package at.jku.isse.gitecco.tree.util;

import at.jku.isse.gitecco.git.Change;
import at.jku.isse.gitecco.tree.nodes.*;
import at.jku.isse.gitecco.tree.visitor.GetNodesForChangeVisitor;
import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChangeComputation {
    private final Set<String> changed;
    private final Set<String> affected;

    public ChangeComputation() {
        this.affected = new HashSet<>();
        this.changed = new HashSet<>();
    }

    public Set<String> getAffected() {
        return Collections.unmodifiableSet(affected);
    }

    public Set<String> getChanged() {
        return Collections.unmodifiableSet(changed);
    }

    public void computeForChange(Change c, SourceFileNode sfn) throws ParserException {
        affected.clear();
        changed.clear();

        final GetNodesForChangeVisitor v = new GetNodesForChangeVisitor(c);
        sfn.accept(v);

        if(v.getchangedNodes().size() == 0) changed.add("BASE");
        else {
            for(ConditionalNode node : v.getchangedNodes()) {
                //Changed: get first positive condition
                while(!checkPositive(node.getCondition()) && node.getParent().getParent()!=null) {
                    node = node.getParent().getParent();
                }
                changed.add(node.getCondition());

                //TODO: IF + ELSE Added --> 2 commits, find out which of them should be seperated.

                //Affected
                while(node.getParent().getParent()!=null) {
                    node = node.getParent().getParent();
                    if(!changed.contains(node.getCondition()))affected.add(node.getCondition());
                }
            }
        }
    }

    /**
     * Determines if a condition is positive --> if its model has positive literals
     * @param condition
     * @return
     * @throws ParserException
     */
    private boolean checkPositive(String condition) throws ParserException {
        final FormulaFactory f = new FormulaFactory();
        final PropositionalParser p = new PropositionalParser(f);
        final Formula formula = p.parse(condition);
        final SATSolver miniSat = MiniSat.miniSat(f);
        miniSat.add(formula);
        final Tristate result = miniSat.sat();

        if(result.equals(Tristate.FALSE)) return false;

        Assignment model = miniSat.model();

        return model.positiveLiterals().size() > 0;
    }

    /*
        for (ConditionalNode node : v.getchangedNodes()) {
            if (node instanceof ELSECondition) {
                String cond = "~";
                cond += node.getParent().getIfBlock().getCondition();
                for (IFCondition elseCond : node.getParent().getElseIfBlocks()) {
                    cond += "~"+elseCond.getCondition();
                }
                System.out.println(cond);
            } else System.out.println(node.getCondition());
       }
     */
}
