package at.jku.isse.gitecco.featureid;

import at.jku.isse.gitecco.core.type.Feature;

public class Experiment {


    public static void main(String... args) throws Exception {
        Feature
                .parseCondition(" A && B || MIN(40, X) < 10")
                .forEach(System.out::println);
    }

}
