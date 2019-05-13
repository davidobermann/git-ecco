package at.jku.isse.gitecco.featureid;

import at.jku.isse.gitecco.core.type.Feature;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Set;
import java.util.stream.Collectors;

public class Exp {
    public static void main(String... args) {
        String s = "E_ENABLE_PIN > -1";
        parseCondition(s);
    }

    /**
     * Extracts all features from a given condition string
     * @param condition the condition string
     * @return A Set of type feature
     */
    public static Set<Feature> parseCondition(String condition) {
        return new Expression(condition)
                .getCopyOfInitialTokens()
                .stream()
                .filter(x->x.looksLike.equals("argument"))
                .map(x->new Feature(x.tokenStr))
                .collect(Collectors.toSet());
    }
}
