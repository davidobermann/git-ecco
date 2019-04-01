package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.conditionparser.ConditionParser;
import at.jku.isse.gitecco.conditionparser.Feature;
import at.jku.isse.gitecco.tree.nodes.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GetGlobalFeatures implements TreeVisitor {

    private final ArrayList<DefineNodes> definitions;
    private final Set<Feature> allFeatures;

    public GetGlobalFeatures() {
        definitions = new ArrayList<>();
        allFeatures = new HashSet<>();
    }

    public List<String> getGLobal() {
        List<String> ret = new ArrayList<>();

        for (Feature f : allFeatures) {
            //if(f.compareToDefine(null))
        }

        return ret;
    }

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
