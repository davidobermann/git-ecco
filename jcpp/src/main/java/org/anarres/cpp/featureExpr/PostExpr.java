package org.anarres.cpp.featureExpr;

import java.util.LinkedList;
import java.util.List;

public class PostExpr extends UnaryExpr {

    public PostExpr(FeatureExpression expr, SingleTokenExpr operator) {
        super(expr, operator);
    }

    public List<FeatureExpression> getChildren() {
        List<FeatureExpression> children = new LinkedList<FeatureExpression>();
        children.add(getExpr());
        children.add(getOperator());
        return children;
    }

    @Override
    public String toString() {
        return "" + getExpr() + getOperator();
    }
}
