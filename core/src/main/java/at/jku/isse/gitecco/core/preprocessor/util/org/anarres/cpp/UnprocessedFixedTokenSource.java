package at.jku.isse.gitecco.core.preprocessor.util.org.anarres.cpp;

import java.util.List;

public class UnprocessedFixedTokenSource extends  FixedTokenSource {

    public UnprocessedFixedTokenSource(Token... tokens) {
        super(tokens);
    }

    public UnprocessedFixedTokenSource(List<Token> tokens) {
        super(tokens);
    }
}
