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
    private final Set<ComittableChange> changed;

    public ChangeComputation() {
        this.changed = new HashSet<>();
    }

    public Set<ComittableChange> getChanged() {
        return Collections.unmodifiableSet(changed);
    }

    /**
     * Computes the configuration + the commit configuration for a given change and a given SourceFileNode
     * @param c
     * @param sfn
     * @throws ParserException
     */
    public void computeForChange(Change c, SourceFileNode sfn) throws ParserException {
        changed.clear();

        final Set<String> affected = new HashSet<>();
        final GetNodesForChangeVisitor v = new GetNodesForChangeVisitor(c);
        sfn.accept(v);

        if(v.getchangedNodes().size() == 0) changed.add(new ComittableChange("BASE", Collections.EMPTY_SET));
        else {
            for(ConditionalNode node : v.getchangedNodes()) {
                //Changed: get first positive condition
                while(!checkPositive(node.getCondition()) && node.getParent().getParent()!=null) {
                    node = node.getParent().getParent();
                }

                //Condition of the first positive node.
                String cond = node.getCondition();

                //Affected
                affected.clear();
                while(node.getParent().getParent()!=null) {
                    node = node.getParent().getParent();
                    affected.add(node.getCondition());
                }

                changed.add(new ComittableChange(cond, affected));
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