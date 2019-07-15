package org.anarres.cpp.featureExpr;

import java.util.LinkedList;
import java.util.List;

public class ParenthesizedExpr extends FeatureExpression {

    private FeatureExpression expr;

    public ParenthesizedExpr(FeatureExpression expr) {
        super();
        setExpr(expr);
    }

    public List<FeatureExpression> getChildren() {
        List<FeatureExpression> children = new LinkedList<FeatureExpression>();
        children.add(expr);
        return children;
    }

    public void setExpr(FeatureExpression expr) {
        this.expr = expr;
        this.expr.setParent(this);
    }

    public FeatureExpression getExpr() {
        return this.expr;
    }

    @Override
    public String toString() {
        return "(" + expr + ")";
    }

    public boolean replace(FeatureExpression child, FeatureExpression newChild) {
        if(child == this.expr){
            setExpr(newChild);
            return true;
        }
        return false;
    }
}
