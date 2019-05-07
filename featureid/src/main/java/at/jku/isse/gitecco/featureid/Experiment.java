package at.jku.isse.gitecco.featureid;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.core.type.TraceableFeature;

import java.util.HashSet;
import java.util.Set;

public class Experiment {


    public static void main(String... args) throws Exception {
        TraceableFeature tf = new TraceableFeature("A");
        Set<TraceableFeature> set = new HashSet<>();
        set.add(tf);

        System.out.println(set.contains(new Feature("A")));
    }

}
