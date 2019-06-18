package at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.featureExpr;

import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.Token;

import java.util.LinkedList;
import java.util.List;

public class SingleTokenExpr extends FeatureExpression {

    private final Token token;

    public SingleTokenExpr(Token token) {
        super();
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public List<FeatureExpression> getChildren() {
        return new LinkedList<FeatureExpression>();
    }

    public String toString() {
        return token.getText();
    }

    public boolean replace(FeatureExpression child, FeatureExpression newChild) {
        return false;
    }
}
