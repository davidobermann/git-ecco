package at.jku.isse.gitecco.featureid;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.core.tree.nodes.DefineNodes;
import at.jku.isse.gitecco.featureid.featuretree.visitor.GetFeaturesAndDefinesVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeatureTracer {

    private final Set<TraceableFeature> traceableFeatures;

    public FeatureTracer() {
        this.traceableFeatures = new HashSet<>();
    }

    public void addToTrace(GetFeaturesAndDefinesVisitor v) {
        boolean occuredBefore = false;
        Set<Feature> features = v.getAllFeatures();
        List<DefineNodes> definitions = v.getDefinitions();

        for (Feature f : features) {

        }
    }

}
