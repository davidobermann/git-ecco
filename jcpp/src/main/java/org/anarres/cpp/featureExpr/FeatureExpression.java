package org.anarres.cpp.featureExpr;

import java.util.List;

public abstract class FeatureExpression {

    private FeatureExpression parent;

    public FeatureExpression getParent() {
        return parent;
    }

    protected void setParent(FeatureExpression parent) {
        this.parent = parent;
    }

    public abstract List<FeatureExpression> getChildren();

    public abstract String toString();

    public abstract boolean replace(FeatureExpression child, FeatureExpression newChild);

    public void traverse(FeatureExpressionTraversal traversal){
        traversal.preVisit(this);
        for(FeatureExpression child : getChildren()){
            child.traverse(traversal);
        }
        traversal.postVisit(this);
    }
}
