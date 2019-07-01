package at.jku.isse.gitecco.translation.constraintcomputation;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.translation.preprocessor.PreprocessorHelper;

import java.io.File;
import java.util.*;

public class SolverAndPreProcExperiments {

    public static void main(String... args) {
        testPPCleaner();
        testPPP();
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
        String expr = "!A && B || C <= 100 || (C > 100 ? A : B)";
        //String expr = "!a&&b||a&&!b||c<10";
        //String expr = "(!a&&b||a&&!b)&&c<10";

        ExpressionSolver es = new ExpressionSolver(expr);
        Map<Feature, Integer> assignment = es.solve();

        assignment.entrySet().forEach(x->System.out.println(x.getKey().getName() + " = " + x.getValue().toString()));
    }
}
