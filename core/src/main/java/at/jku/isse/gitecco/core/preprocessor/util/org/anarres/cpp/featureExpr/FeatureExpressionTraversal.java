package at.jku.isse.gitecco.core.preprocessor.util.org.anarres.cpp.featureExpr;

public interface FeatureExpressionTraversal {

    void preVisit(FeatureExpression expr);

    void postVisit(FeatureExpression expr);
}
