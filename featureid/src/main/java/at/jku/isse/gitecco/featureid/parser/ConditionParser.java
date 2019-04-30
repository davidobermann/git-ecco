package at.jku.isse.gitecco.featureid.parser;

import at.jku.isse.gitecco.core.type.Feature;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Set;
import java.util.stream.Collectors;


public class ConditionParser {

    public static Set<Feature> parseCondition(String condition) {
        return new Expression(condition)
                .getCopyOfInitialTokens()
                .stream()
                .filter(x->x.looksLike.equals("argument"))
                .map(x->new Feature(x.tokenStr))
                .collect(Collectors.toSet());
    }

}
