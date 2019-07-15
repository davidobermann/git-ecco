package org.anarres.cpp.featureExpr;

public interface FeatureExpressionTraversal {

    void preVisit(FeatureExpression expr);

    void postVisit(FeatureExpression expr);
}
