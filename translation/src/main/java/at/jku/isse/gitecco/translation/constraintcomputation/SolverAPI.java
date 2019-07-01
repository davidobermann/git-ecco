package at.jku.isse.gitecco.translation.constraintcomputation;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.translation.preprocessor.PreprocessorHelper;

import java.util.*;

public class SolverAPI {

    public static void main(String... args) {
        //testPP();
        testFeatureExpParser();
    }

    private static void testPP() {
        PreprocessorHelper pph = new PreprocessorHelper();
        //TODO: Test
        //pph.generateCleanVersion();
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
