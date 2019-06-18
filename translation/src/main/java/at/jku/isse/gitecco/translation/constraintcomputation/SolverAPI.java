package at.jku.isse.gitecco.translation.constraintcomputation;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.translation.preprocessor.PreprocessorHelper;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import static org.chocosolver.solver.constraints.nary.cnf.LogOp.*;

public class SolverAPI {
    public static void main(String... args) {
        //testChoco();
        testPP();
    }

    private static void testPP() {
        PreprocessorHelper pph = new PreprocessorHelper();
        //TODO: Test
        //pph.generateCleanVersion();
    }


    private static void testChoco() {
        //BoolVar a = model.boolVar("a");
        //BoolVar b = model.boolVar("b");
        //BoolVar c = model.boolVar("c");
        //IntVar num = model.intVar(10);
        //IntVar x = model.intVar(Short.MIN_VALUE, Short.MAX_VALUE);
        //model.and(a,b);
        //model.addClauses(and(and(and(a,c),and(b.not(),c)),x.lt(10).boolVar()));
        //model.addClauseTrue(x.lt(10).boolVar());
        //model.addClauseTrue(x.gt(5).boolVar());
        //model.addClauseTrue(a.not());

        Model model = new Model("experiment");
        //test: #ifdef !a&&b||a&&!b||c<10

        /*#ifdef !a&&b||a&&!b||c<10
            int i;
            i++;
        #endif

                a, !b, c=5*/

        BoolVar a = model.boolVar("a");
        BoolVar b = model.boolVar("b");
        IntVar  c = model.intVar("c", Short.MIN_VALUE, Short.MAX_VALUE);

        Constraint c1 = model.and(a.not(), b);
        Constraint c2 = model.and(b.not(), a);
        Constraint c3 = model.or(c.lt(10).boolVar());
        Constraint constraint = model.or(c1,c2,c3);


        model.post(constraint);

        Solution solution = model.getSolver().findSolution();

        System.out.println(b.getBooleanValue());

        if (solution != null) {
            System.out.println(solution.toString());
        }

        /*
         * Idea of solving:
         *  - create variables
         *  - create constraints
         *  - post constraint to model
         *  - extract solution for created variables
         *
         * Possible challenges:
         *  - variable names
         *  - decomposing the formula correctly
         */

    }
}
