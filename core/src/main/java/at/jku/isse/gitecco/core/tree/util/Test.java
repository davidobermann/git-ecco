package at.jku.isse.gitecco.core.tree.util;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.*;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.io.parsers.PseudoBooleanParser;
import org.logicng.pseudobooleans.PBConfig;
import org.logicng.pseudobooleans.PBEncoder;
import org.logicng.solvers.MiniSat;

import java.util.List;

public class Test {
    public static void main(String... args) throws ParserException {
        String test = "(A && B) && (X < 10)";

        test = test.replace("&&","&").replace("||","|").replace("!","~");

        FormulaFactory f = new FormulaFactory();
        PBEncoder encoder
                = new PBEncoder(f, new PBConfig.Builder().pbEncoding(PBConfig.PB_ENCODER.ADDER_NETWORKS).build());
        PseudoBooleanParser p = new PseudoBooleanParser(f);

        MiniSat solver = MiniSat.miniSat(f);

        //p.parse(test).bdd().model().positiveLiterals().forEach(System.out::println);

        solver.add(f.parse(test));
        //solver.add(p.parse(test));

        //int[] coeff = {1};
        //solver.add(encoder.encode(f.pbc(CType.LT,10,new Literal[] {f.variable("X")},coeff)));

        //System.out.println(solver.enumerateAllModels().size());

        //List<Assignment> models = solver.enumerateAllModels();

        //System.out.println(solver.sat().equals(Tristate.TRUE));
        /*for (Assignment assignment : solver.enumerateAllModels(p.parse(test).variables())) {
            assignment.positiveLiterals().forEach(System.out::println);
            assignment.negativeLiterals().forEach(System.out::println);
        }*/

        System.out.println("all models:");
        for (Assignment assignment : solver.enumerateAllModels()) {
            System.out.println("-----------------------------------");
            assignment.positiveLiterals().forEach(System.out::println);
            assignment.negativeLiterals().forEach(System.out::println);
        }

        //final List<Assignment> models = solver.enumerateAllModels(literals100);

    }
}
