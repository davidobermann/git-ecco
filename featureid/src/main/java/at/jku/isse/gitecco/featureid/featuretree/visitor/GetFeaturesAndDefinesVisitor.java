package at.jku.isse.gitecco.featureid.featuretree.visitor;

import at.jku.isse.gitecco.featureid.parser.ConditionParser;
import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.core.tree.nodes.*;
import at.jku.isse.gitecco.core.tree.visitor.TreeVisitor;

import java.util.*;

public class GetFeaturesAndDefinesVisitor implements TreeVisitor {

    private final ArrayList<DefineNodes> definitions;
    private final Set<Feature> allFeatures;

    public GetFeaturesAndDefinesVisitor() {
        definitions = new ArrayList<>();
        allFeatures = new HashSet<>();
    }

    public Set<Feature> getAllFeatures() {
        return allFeatures;
    }

    public List<DefineNodes> getDefinitions() {
        return definitions;
    }

    /**
     * Retrieves all global features of the tree,
     * after passing the visitor through the tree.
     * @return
     */
    public Set<Feature> getGlobal() {
        Set<Feature> global = new HashSet<>();

        for (Feature f : allFeatures) {
            if(!comp(definitions, f)) global.add(f);
        }

        return Collections.unmodifiableSet(global);
    }

    /**
     * Helper method: checks if the feature
     * is internally defined some time in the code.
     * Very inefficient, maybe a better solution possible.
     * @param defines
     * @param f
     * @return
     */
    private boolean comp(List<DefineNodes> defines, Feature f) {
        for (DefineNodes d : defines) {
            if(f.compareToDefine(d)) return true;
        }
        return false;
    }

    /* Visitor methods:*/

    @Override
    public void visit(RootNode n) {

    }

    @Override
    public void visit(BinaryFileNode n) {

    }

    @Override
    public void visit(SourceFileNode n) {

    }

    @Override
    public void visit(ConditionBlockNode n) {

    }

    @Override
    public void visit(IFCondition c) {
        allFeatures.addAll(ConditionParser.parseCondition(c.getCondition()));
        definitions.addAll(c.getDefineNodes());
    }

    @Override
    public void visit(IFDEFCondition c) {
        allFeatures.addAll(ConditionParser.parseCondition(c.getCondition()));
        definitions.addAll(c.getDefineNodes());
    }

    @Override
    public void visit(IFNDEFCondition c) {
        allFeatures.addAll(ConditionParser.parseCondition(c.getCondition()));
        definitions.addAll(c.getDefineNodes());
    }

    @Override
    public void visit(ELSECondition c) {
        allFeatures.addAll(ConditionParser.parseCondition(c.getCondition()));
        definitions.addAll(c.getDefineNodes());
    }
}
