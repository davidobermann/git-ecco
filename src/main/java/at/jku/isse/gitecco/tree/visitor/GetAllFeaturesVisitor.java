package at.jku.isse.gitecco.tree.visitor;

import at.jku.isse.gitecco.conditionparser.ConditionParser;
import at.jku.isse.gitecco.tree.nodes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetAllFeaturesVisitor implements TreeVisitor {
    private final List<String> featureNames;

    public GetAllFeaturesVisitor() {
        this.featureNames = new ArrayList<>();
    }

    public Collection<String> getAllFeatures() {
        return featureNames;
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
        featureNames.addAll(
                ConditionParser.parseCondition(c.getCondition())
        );
    }

    @Override
    public void visit(IFDEFCondition c) {
        featureNames.addAll(
                ConditionParser.parseCondition(c.getCondition())
        );
    }

    @Override
    public void visit(IFNDEFCondition c) {
        featureNames.addAll(
                ConditionParser.parseCondition(c.getCondition())
        );

    }

    @Override
    public void visit(ELSECondition c) {
    }
}
