package at.jku.isse.gitecco.featureid.identification;

import at.jku.isse.gitecco.core.tree.nodes.DefineNodes;
import at.jku.isse.gitecco.core.tree.nodes.FileNode;
import at.jku.isse.gitecco.core.tree.nodes.RootNode;
import at.jku.isse.gitecco.core.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.core.type.FeatureType;
import at.jku.isse.gitecco.core.type.TraceableFeature;
import at.jku.isse.gitecco.featureid.featuretree.visitor.GetAllFeaturesAndDefinesVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ID {

    /**
     * Identifies all features and their types that occur in a tree of a commit.
     * @param tree
     * @return
     */
    public static Map<Feature, FeatureType> id(RootNode tree) {
        /*
         * idea:
         *        - traverse the tree for each file
         *        - for each file iterate over all found features
         *        - for each feature iterate over all defines and compare their line value
         *        - if a define occurs before the use of the feature --> internal
         *        - if a feature is used without a single define of it in the file --> external
         *        - if a feature is used before a define of it in the same file --> transient
         *        - every time a value would be stored in the map check for the current value
         *        - only override if the type is weaker than the one in the map
         *        - after this procedure the map contains all features and its type
         *          for one commit.
         *
         *       Further steps: continue with evaluating the map --> translating this result into a set of
         *                      traceable features.
         *
         */
        Map<Feature, FeatureType> featureMap = new HashMap<>();
        GetAllFeaturesAndDefinesVisitor v = new GetAllFeaturesAndDefinesVisitor();
        FeatureType type = null;

        for (FileNode child : tree.getChildren()) {
            v.reset();

            if(child instanceof SourceFileNode) {
                child.accept(v);

                for (Map.Entry<Feature, Integer> entry : v.getFeatureMap().entrySet()) {
                    for (DefineNodes define : v.getDefines()) {
                        type = featureMap.get(entry.getKey());
                        boolean sameName = entry.getKey().getName().equals(define.getMacroName());
                        int lineResult = entry.getValue().compareTo(define.getLineInfo());
                        if(sameName && lineResult < 0) {
                            //feature is defined but also appears before its define:
                            featureMap.put(entry.getKey(), FeatureType.TRANSIENT);
                            System.err.println("Feature: " + entry.getKey().getName() + " is transient at some point!");
                            break;
                        } else if(sameName && lineResult >= 0) {
                            //feature is defined, and never (at least until this define) appears before its define:
                            if(type == null || !type.equals(FeatureType.TRANSIENT))
                                featureMap.put(entry.getKey(), FeatureType.INTERNAL);
                        }
                    }

                    type = featureMap.get(entry.getKey());
                    //if the feature has never been defined in this file it is external:
                    if(type == null || type.equals(FeatureType.EXTERNAL))
                        featureMap.put(entry.getKey(), FeatureType.EXTERNAL);
                }
            }
        }
        return featureMap;
    }

    /**
     * Evaluates the map that results from analyzing one commit.
     * For every occurrence of a feature the counter is increased corresponding to its type.
     * @param evalList A list of traceable features.
     * @param map The map that results from the id method
     * @return the list that was passed.
     */
    public static List<TraceableFeature> evaluateFeatureMap(List<TraceableFeature> evalList, Map<Feature, FeatureType> map) {
        for (Map.Entry<Feature, FeatureType> entry : map.entrySet()) {
            if(evalList.contains(entry.getKey())) {
                evalList.get(evalList.indexOf(entry.getKey())).inc(entry.getValue());
            } else {
                evalList.add(new TraceableFeature(entry.getKey()).inc(entry.getValue()));
            }
        }
        return evalList;
    }

}
