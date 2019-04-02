package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.conditionparser.ConditionParser;
import at.jku.isse.gitecco.conditionparser.Feature;
import at.jku.isse.gitecco.tree.nodes.*;

import java.util.*;

public class GetGlobalFeaturesVisitor implements TreeVisitor {

    private final ArrayList<DefineNodes> definitions;
    private final Set<Feature> allFeatures;

    public GetGlobalFeaturesVisitor() {
        definitions = new ArrayList<>();
        allFeatures = new HashSet<>();
    }

    /**
     * Retrieves all global features of the tree,
     * after passing the visitor through the tree.
     * @return
     */
    public List<Feature> getGlobal() {
        List<Feature> global = new ArrayList<>();

        for (Feature f : allFeatures) {
            if(!comp(definitions, f)) global.add(f);
        }

        return Collections.unmodifiableList(global);
    }

    /**
     * Helper method: checks if the feaure
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
