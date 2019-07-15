package org.anarres.cpp.featureExpr;

import java.util.LinkedList;
import java.util.List;

public class MacroCall extends FeatureExpression{

    private Name name;

    private List<FeatureExpression> arguments;

    public MacroCall(Name name, List<FeatureExpression> arguments) {
        super();
        setName(name);
        this.arguments = arguments;
        for(FeatureExpression arg :this.arguments){
            arg.setParent(this);
        }
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
        this.name.setParent(this);
    }

    public List<FeatureExpression> getArguments() {
        List<FeatureExpression> clone = new LinkedList<FeatureExpression>();
        clone.addAll(arguments);
        return clone;
    }

    public List<FeatureExpression> getChildren() {
        List<FeatureExpression> children = new LinkedList<FeatureExpression>();
        children.add(name);
        children.addAll(arguments);
        return children;
    }

    @Override
    public String toString() {
        String args = "";
        boolean first = true;
        for(FeatureExpression arg : arguments){
            if(!first){
                args += ", ";
            }
            args += arg;
            first = false;
        }
        return name + "(" + args + ")";
    }

    public boolean replace(FeatureExpression child, FeatureExpression newChild) {
        if(child == this.name){
            if(newChild instanceof Name){
                setName((Name)newChild);
                return true;
            }
        }
        if(this.arguments.contains(child)){
            int index = this.arguments.indexOf(child);
            this.arguments.remove(index);
            this.arguments.add(index, newChild);
            return true;
        }
        return false;
    }
}
