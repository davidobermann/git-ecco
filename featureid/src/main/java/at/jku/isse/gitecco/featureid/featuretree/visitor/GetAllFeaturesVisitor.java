package at.jku.isse.gitecco.featureid.featuretree.visitor;


import at.jku.isse.gitecco.featureid.TraceableFeature;

import java.util.HashSet;
import java.util.Set;

public class GetAllFeaturesVisitor {
    private final Set<TraceableFeature> features;

    public GetAllFeaturesVisitor() {
        features  = new HashSet<>();
    }


}
