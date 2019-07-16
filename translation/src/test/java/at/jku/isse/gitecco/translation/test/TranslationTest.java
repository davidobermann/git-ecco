package at.jku.isse.gitecco.translation.test;

import at.jku.isse.gitecco.core.solver.ExpressionSolver;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.junit.*;

public class TranslationTest {

    @Test
    public void testSolver() {
        String condition = "(C>5) && A";
        String ifc = "X";
        String then = "C=6";
        ExpressionSolver es = new ExpressionSolver(condition);
        es.addFeatureImplication(ifc,then);
        es.solve().entrySet().forEach(x->System.out.println(x.getKey() + " = " + x.getValue()));
    }

    @Test
    public void chocoSolverExperiment() {
        Model model = new Model("test");
        IntVar c = model.intVar("C", Short.MIN_VALUE, Short.MAX_VALUE);
        //IntVar c = checkVars(model, "C").asIntVar();
        model.post(c.gt(4).boolVar().and(model.boolVar("A")).extension());
        Variable var = checkVars(model, "C");
        model.ifOnlyIf(model.boolVar("X").extension(),model.arithm(var.asIntVar(),"=",6));
        Solution solution = model.getSolver().findSolution();
        System.out.println(solution);
    }

    private Variable checkVars(Model model, String name) {
        for (Variable var : model.getVars()) {
            if (var.getName().equals(name)) return var;
        }
        return null;
    }
}
