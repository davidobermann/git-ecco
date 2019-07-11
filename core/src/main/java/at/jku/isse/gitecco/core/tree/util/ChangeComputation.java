package at.jku.isse.gitecco.core.tree.util;

import at.jku.isse.gitecco.core.git.Change;
import at.jku.isse.gitecco.core.solver.ExpressionSolver;
import at.jku.isse.gitecco.core.tree.nodes.ConditionalNode;
import at.jku.isse.gitecco.core.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.core.tree.visitor.GetNodesForChangeVisitor;

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
     *
     * @param c
     * @param sfn
     */
    public void computeForChange(Change c, SourceFileNode sfn) {
        changed.clear();

        final Set<String> affected = new HashSet<>();
        final GetNodesForChangeVisitor v = new GetNodesForChangeVisitor(c);
        sfn.accept(v);

        if (v.getchangedNodes().size() == 0) changed.add(new ComittableChange("BASE", Collections.EMPTY_SET));
        else {
            for (ConditionalNode node : v.getchangedNodes()) {
                //Changed: get first positive condition
                while (!checkPositive(node.getCondition()) && node.getParent().getParent() != null) {
                    node = node.getParent().getParent();
                }

                //Condition of the first positive node.
                String cond = node.getCondition();

                //Affected
                affected.clear();
                while (node.getParent().getParent() != null) {
                    node = node.getParent().getParent();
                    affected.add(node.getCondition());
                }

                changed.add(new ComittableChange(cond, affected));
            }
        }
    }

    /**
     * Checks if the given condition has a positive solution.
     * Positive Solution: At least one variable has an assignment that is not false(0).
     *
     * @param condition
     * @return
     */
    private boolean checkPositive(String condition) {
        return new ExpressionSolver(condition)
                        .solve()
                        .entrySet()
                        .stream()
                        .filter(x->x.getValue()!=0)
                        .count()
                        > 0;
    }
}
