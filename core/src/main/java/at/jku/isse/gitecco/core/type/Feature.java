package at.jku.isse.gitecco.core.type;

import at.jku.isse.gitecco.core.tree.nodes.DefineNodes;

import java.util.Objects;

public class Feature implements Comparable<Feature> {
    private final String name;

    public Feature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

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
}
