package at.jku.isse.gitecco.core.type;

import at.jku.isse.gitecco.core.tree.nodes.DefineNodes;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to represent a feature.
 */
public class Feature implements Comparable<Feature> {
    private final String name;

    public Feature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Compares a #define or #undef to this feature
     * @param n
     * @return
     */
    public boolean compareToDefine(DefineNodes n) {
        return this.name.equals(n.getMacroName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return Objects.equals(name, feature.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int compareTo(Feature o) {
        return this.name.compareTo(o.name);
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
