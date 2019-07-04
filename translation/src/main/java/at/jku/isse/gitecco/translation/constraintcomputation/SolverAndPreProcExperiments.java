package at.jku.isse.gitecco.translation.constraintcomputation;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.translation.preprocessor.PreprocessorHelper;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.Variable;

import java.io.File;
import java.util.*;

public class SolverAndPreProcExperiments {

    public static void main(String... args) {
        //testPPCleaner();
        //testPPP();
        testFeatureExpParser();
    }

    private static void testPPCleaner() {
        PreprocessorHelper pph = new PreprocessorHelper();
        String inPath = "C:\\obermanndavid\\git-ecco\\ppfiles\\test\\git";
        String clean = "C:\\obermanndavid\\git-ecco\\ppfiles\\test\\clean";
        pph.generateCleanVersion(new File(inPath), new File(clean));
    }

    private static void testPPP() {
        PreprocessorHelper pph = new PreprocessorHelper();
        String inPath = "C:\\obermanndavid\\git-ecco\\ppfiles\\test\\git";
        String ecco = "C:\\obermanndavid\\git-ecco\\ppfiles\\test\\ecco";
        Map<Feature, Integer> config = new HashMap<>();
        config.put(new Feature("A"), 1);
        pph.generateVariants(config, new File(inPath), new File(ecco));
    }

    private static void testFeatureExpParser() {
        //String expr = "!A && B || C <= 100 || (C > 100 ? A : B)";
        //String expr = "!a&&b||a&&!b||c<10";
        //String expr = "(!a&&b||a&&!b)&&
        String expr = "(!A || B) && (!A || (C==9)) && B && (C>5)";

        /*
        //how to model implicated features: not via implication --> ifthen
        Model model = new Model("test");
        Variable a = model.boolVar("A");
        Variable b = model.boolVar("B");

        model.post(a.asBoolVar().extension());

        model.ifThen(a.asBoolVar(), b.asBoolVar().extension());
        model.ifThen(model.boolVar("true",true), b.asBoolVar().not().extension());

        Solution s = model.getSolver().findSolution();

        System.out.println(s);*/

        ExpressionSolver es = new ExpressionSolver(expr);
        Map<Feature, Integer> assignment = es.solve();

        assignment.entrySet().forEach(x->System.out.println(x.getKey().getName() + " = " + x.getValue().toString()));
    }
}
