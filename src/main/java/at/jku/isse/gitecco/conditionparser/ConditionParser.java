package at.jku.isse.gitecco.conditionparser;

import at.jku.isse.gitecco.cdt.Feature;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.parsertokens.BinaryRelation;
import org.mariuszgromada.math.mxparser.parsertokens.ParserSymbol;
import org.mariuszgromada.math.mxparser.parsertokens.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for parsing the condition of an IASTPreprocessorStatement.
 */
public class ConditionParser {

    private static final int PLUS_OFFSET = 10;
    private static final int MINUS_OFFSET = -10;

    public ConditionParser() {

    }

    /**
     * Parses the given condition for possible features and
     * if existing the corresponding definition for preprocessing.
     *
     * @param condition The condition to be parsed.
     * @return
     */
    public Feature[] parseCondition(String condition) {
        Expression exp = new Expression(condition);
        Token[] tokens = exp.getCopyOfInitialTokens().toArray(new Token[exp.getCopyOfInitialTokens().size()]);
        List<ExtractedFeature> features = new ArrayList<>();

        String leveledExpression = "";
        int lvlbuf = 0;
        int i = 0;
        for (Token token : tokens) {
            if (token.tokenLevel == lvlbuf) {
                leveledExpression += (token.tokenTypeId == ParserSymbol.TYPE_ID)
                        || (token.looksLike.equals("function"))
                        || (token.tokenTypeId == 1) ? "" : token.tokenStr;
                if (i == tokens.length-1) {
                    if (leveledExpression.length() > 0) System.out.println(leveledExpression);
                    parseLeveledExpression(leveledExpression);
                }
            } else {
                if (leveledExpression.length() > 0) System.out.println(leveledExpression);
                parseLeveledExpression(leveledExpression);
                lvlbuf = token.tokenLevel;
                leveledExpression = (token.tokenTypeId == ParserSymbol.TYPE_ID) ? "" : token.tokenStr;// || (token.tokenTypeId == 1)
            }
            i++;
        }

        return features.toArray(new Feature[features.size()]);
    }


    private List<ExtractedFeature> parseLeveledExpression(String expression) {
        int i = 0;
        double offset = 0;
        List<ExtractedFeature> features = new ArrayList<>();

        if (expression.length() > 0) {
            Expression ep = new Expression(expression);
            Token[] tokens = ep.getCopyOfInitialTokens().toArray(new Token[ep.getCopyOfInitialTokens().size()]);
            for (Token t : ep.getCopyOfInitialTokens()) {
                if (t.looksLike.equals("argument")) {
                    if (i == 0) {
                        if (tokens.length == i+1) {
                            System.out.println(t.tokenStr+" must be defined");
                        } else if (tokens[i+1].tokenTypeId != BinaryRelation.TYPE_ID) {
                            System.out.println(t.tokenStr+" must be defined");
                        }
                    } else if ((i-1 >= 0) && (i+1 < tokens.length)) {
                        if (tokens[i-1].tokenTypeId != BinaryRelation.TYPE_ID
                                && tokens[i+1].tokenTypeId != BinaryRelation.TYPE_ID) {
                            System.out.println(t.tokenStr+" must be defined");
                        }
                    } else if (i+1 == tokens.length) {
                        if (i-1 >= 0) {
                            if (tokens[i-1].tokenTypeId != BinaryRelation.TYPE_ID) {
                                System.out.println(t.tokenStr+" must be defined");
                            }
                        } else {
                            System.out.println(t.tokenStr+" must be defined");
                        }
                    }
                } else if (t.tokenTypeId == BinaryRelation.TYPE_ID) {
                    if ((i+1 < tokens.length) && (i-1 >= 0)) {
                        if (t.tokenStr.equals(BinaryRelation.EQ1_STR)
                                || t.tokenStr.equals(BinaryRelation.GEQ_STR)
                                || t.tokenStr.equals(BinaryRelation.LEQ_STR)) {
                            offset = 0;
                        } else if (t.tokenStr.equals(BinaryRelation.GT_STR)) {
                            offset = -10;
                        } else if (t.tokenStr.equals(BinaryRelation.LT_STR)) {
                            offset = 10;
                        }
                        if ((tokens[i-1].tokenTypeId == 0) || tokens[i+1].tokenTypeId == 0) {
                            //Left/Right is an argument
                            if (tokens[i-1].looksLike.equals("argument")) {
                                System.out.println(tokens[i-1].tokenStr+" must be defined as "+(tokens[i+1].tokenValue+offset));
                            } else if (tokens[i+1].looksLike.equals("argument")) {
                                System.out.println(tokens[i+1].tokenStr+" must be defined as "+(tokens[i-1].tokenValue+offset));
                            }
                        }
                    }
                }
                i++;
            }
            //features.add(new ExtractedFeature(tokens[i-1].tokenStr, Optional.ofNullable(tokens[i+1].tokenValue)));
        }
        return features;
    }

}
