package at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.featureExpr;

public interface FeatureExpressionTraversal {

    void preVisit(FeatureExpression expr);

    void postVisit(FeatureExpression expr);
}
