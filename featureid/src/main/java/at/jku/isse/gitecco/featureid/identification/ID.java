package at.jku.isse.gitecco.featureid.identification;

import at.jku.isse.gitecco.core.tree.nodes.DefineNodes;
import at.jku.isse.gitecco.core.tree.nodes.FileNode;
import at.jku.isse.gitecco.core.tree.nodes.RootNode;
import at.jku.isse.gitecco.core.tree.nodes.SourceFileNode;
import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.core.type.TraceableFeature;
import at.jku.isse.gitecco.featureid.featuretree.visitor.GetAllFeaturesAndDefinesVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ID {

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

        for (FileNode child : tree.getChildren()) {
            v.reset();

            if(child instanceof SourceFileNode) {
                child.accept(v);

                for (Map.Entry<Feature, Integer> entry : v.getFeatureMap().entrySet()) {
                    for (DefineNodes define : v.getDefines()) {
                        boolean sameName = entry.getKey().getName().equals(define.getMacroName());
                        int lineResult = entry.getValue().compareTo(define.getLineInfo());
                        if(sameName && lineResult < 0) {
                            //feature is defined but also appears before its define:
                            featureMap.put(entry.getKey(), FeatureType.TRANSIENT);
                            System.err.println("Feature: " + entry.getKey().getName() + " is transient!");
                            break;
                        } else if(sameName && lineResult >= 0){
                            //feature is defined, and never (at least until this define) appears before its define:
                            if(!featureMap.get(entry.getKey()).equals(FeatureType.TRANSIENT))
                                featureMap.put(entry.getKey(), FeatureType.INTERNAL);
                        }
                    }

                    //if the feature has never been defined in this file it is external:
                    if(featureMap.get(entry.getKey()).equals(FeatureType.EXTERNAL))
                        featureMap.put(entry.getKey(), FeatureType.EXTERNAL);
                }
            }
        }
        return featureMap;
    }

    public static List<TraceableFeature> evaluateFeatureMap(List<TraceableFeature> evalList, Map<Feature, FeatureType> map) {

        return evalList;
    }

    /* ugly version:
    public static void id(RootNode tree) {
        Map<FileNode, Map<Feature, FeatureType>> featureMap = new HashMap<>();
        Map<Feature, FeatureType> innerMap;
        GetAllDefinesVisitor dv = new GetAllDefinesVisitor();
        GetAllFeaturesVisitor fv = new GetAllFeaturesVisitor();

        for (FileNode child : tree.getChildren()) {
            dv.reset();
            fv.reset();

            if(child instanceof SourceFileNode) {
                child.accept(dv);
                child.accept(fv);
                innerMap = new HashMap<>();

                for (Map.Entry<Feature, Integer> entry : fv.getFeatureMap().entrySet()) {
                    for (DefineNodes define : dv.getDefines()) {
                        boolean sameName = entry.getKey().getName().equals(define.getMacroName());
                        int lineResult = entry.getValue().compareTo(define.getLineInfo());
                        if(sameName && lineResult < 0) {
                            //feature is defined but also appears before its define:
                            innerMap.put(entry.getKey(), FeatureType.TRANSIENT);
                            System.err.println("Feature: " + entry.getKey().getName() + " is transient!");
                            break;
                        } else if(sameName && lineResult >= 0){
                            //feature is defined, and never (until this define) appears before its define:
                            innerMap.put(entry.getKey(), FeatureType.INTERNAL);
                        }
                    }

                    //if the feature has never been defined in this file it is external:
                    if(!innerMap.containsKey(entry.getKey())) innerMap.put(entry.getKey(), FeatureType.EXTERNAL);
                }
                //eventually link the innerMap to the file node
                featureMap.put(child, Collections.unmodifiableMap(innerMap));
            }
        }
    }
     */

}
