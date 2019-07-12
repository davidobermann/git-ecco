package at.jku.isse.gitecco.core.tree.util;


import at.jku.isse.gitecco.core.types.Feature;

public class Test {
    public static void main(String... args) {
        String exp = "!a && !b && d==0 && __TEST__";
        Feature.parseCondition(exp).forEach(x->System.out.println(x.getName()));
    }
}
