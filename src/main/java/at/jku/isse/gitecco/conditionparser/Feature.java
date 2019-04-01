package at.jku.isse.gitecco.conditionparser;

import at.jku.isse.gitecco.tree.nodes.DefineNodes;

import java.util.Objects;

public class Feature {
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
}
