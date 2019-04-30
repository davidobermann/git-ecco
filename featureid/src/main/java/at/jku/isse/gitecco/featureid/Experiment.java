package at.jku.isse.gitecco.featureid;

import at.jku.isse.gitecco.featureid.parser.ConditionParser;
import org.mariuszgromada.math.mxparser.*;

public class Experiment {


    public static void main(String... args) throws Exception {
        ConditionParser
                .parseCondition(" A && B || MIN(40, X) < 10")
                .forEach(System.out::println);
    }

}
