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
     * Parses the given condition for possible features
     *
     * @param condition The condition to be parsed.
     * @return
     */
    public static ParsedCondition[] parseConditionWithDefition(String condition) {
        Expression exp = new Expression(condition);
        Token[] tokens = exp.getCopyOfInitialTokens().toArray(new Token[exp.getCopyOfInitialTokens().size()]);
        List<ParsedCondition> conditions = new ArrayList<>();

        String leveledExpression = "";
        int lvlbuf = 0;
        int i = 0;
        for (Token token : tokens) {
            if (token.tokenLevel == lvlbuf) {
                leveledExpression += (token.tokenTypeId == ParserSymbol.TYPE_ID)
                        || (token.looksLike.equals("function"))
                        || (token.tokenTypeId == 1) ? "" : token.tokenStr;
                if (i == tokens.length-1) {
                    conditions.addAll(parseLeveledExpressionWithDefine(leveledExpression));
                }
            } else {
                conditions.addAll(parseLeveledExpressionWithDefine(leveledExpression));
                lvlbuf = token.tokenLevel;
                leveledExpression = (token.tokenTypeId == ParserSymbol.TYPE_ID) ? "" : token.tokenStr;
            }
            i++;
        }
        return conditions.toArray(new ParsedCondition[conditions.size()]);
    }

    /**
     * Parses the given condition for possible features and
     * if existing the corresponding definition for preprocessing.
     *
     * @param condition The condition to be parsed.
     * @return
     */
    public static String[] parseCondition(String condition) {
        Expression exp = new Expression(condition);
        Token[] tokens = exp.getCopyOfInitialTokens().toArray(new Token[exp.getCopyOfInitialTokens().size()]);
        List<String> featureNames = new ArrayList<>();

        String leveledExpression = "";
        int lvlbuf = 0;
        int i = 0;
        for (Token token : tokens) {
            if (token.tokenLevel == lvlbuf) {
                leveledExpression += (token.tokenTypeId == ParserSymbol.TYPE_ID)
                        || (token.looksLike.equals("function"))
                        || (token.tokenTypeId == 1) ? "" : token.tokenStr;
                if (i == tokens.length-1) {
                    featureNames.add(parseLeveledExpression(leveledExpression));
                }
            } else {
                featureNames.add(parseLeveledExpression(leveledExpression));
                lvlbuf = token.tokenLevel;
                leveledExpression = (token.tokenTypeId == ParserSymbol.TYPE_ID) ? "" : token.tokenStr;
            }
            i++;
        }

        return featureNames.toArray(new String[featureNames.size()]);
    }

    /**
     * Currently not needed:
     * Extracts the Features + its definition if there is one.
     * @param expression
     * @return
     */
    private static List<ParsedCondition> parseLeveledExpressionWithDefine(String expression) {
        int i = 0;
        double offset = 0;
        List<ParsedCondition> parsedCond = new ArrayList<>();

        if (expression.length() > 0) {
            Expression ep = new Expression(expression);
            Token[] tokens = ep.getCopyOfInitialTokens().toArray(new Token[ep.getCopyOfInitialTokens().size()]);
            for (Token t : ep.getCopyOfInitialTokens()) {
                if (t.looksLike.equals("argument")) {
                    if (i == 0) {
                        if (tokens.length == i+1) {
                            System.out.println(t.tokenStr+" must be defined");
                            parsedCond.add(new ParsedCondition(t.tokenStr, 1));
                        } else if (tokens[i+1].tokenTypeId != BinaryRelation.TYPE_ID) {
                            System.out.println(t.tokenStr+" must be defined");
                            parsedCond.add(new ParsedCondition(t.tokenStr, 1));
                        }
                    } else if ((i-1 >= 0) && (i+1 < tokens.length)) {
                        if (tokens[i-1].tokenTypeId != BinaryRelation.TYPE_ID
                                && tokens[i+1].tokenTypeId != BinaryRelation.TYPE_ID) {
                            System.out.println(t.tokenStr+" must be defined");
                            parsedCond.add(new ParsedCondition(t.tokenStr, 1));
                        }
                    } else if (i+1 == tokens.length) {
                        if (i-1 >= 0) {
                            if (tokens[i-1].tokenTypeId != BinaryRelation.TYPE_ID) {
                                System.out.println(t.tokenStr+" must be defined");
                                parsedCond.add(new ParsedCondition(t.tokenStr, 1));
                            }
                        } else {
                            System.out.println(t.tokenStr+" must be defined");
                            parsedCond.add(new ParsedCondition(t.tokenStr, 1));
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
                                parsedCond.add(new ParsedCondition(tokens[i-1].tokenStr, tokens[i+1].tokenValue));
                            } else if (tokens[i+1].looksLike.equals("argument")) {
                                System.out.println(tokens[i+1].tokenStr+" must be defined as "+(tokens[i-1].tokenValue+offset));
                                parsedCond.add(new ParsedCondition(tokens[i+1].tokenStr, tokens[i-1].tokenValue));
                            }
                        }
                    }
                }
                i++;
            }
            //features.add(new ExtractedFeature(tokens[i-1].tokenStr, Optional.ofNullable(tokens[i+1].tokenValue)));
            //parsedCond.add(new ParsedCondition(t.tokenStr, 1));
        }
        return parsedCond;
    }


    private static String parseLeveledExpression(String expression) {
        int i = 0;
        if (expression.length() > 0) {
            Expression ep = new Expression(expression);
            expression = "";
            Token[] tokens = ep.getCopyOfInitialTokens().toArray(new Token[ep.getCopyOfInitialTokens().size()]);
            for (Token t : ep.getCopyOfInitialTokens()) {
                if (t.looksLike.equals("argument")) {
                    if (i == 0) {
                        if (tokens.length == i+1) {
                            expression = t.tokenStr;
                        } else if (tokens[i+1].tokenTypeId != BinaryRelation.TYPE_ID) {
                            expression = t.tokenStr;
                        }
                    } else if ((i-1 >= 0) && (i+1 < tokens.length)) {
                        if (tokens[i-1].tokenTypeId != BinaryRelation.TYPE_ID
                                && tokens[i+1].tokenTypeId != BinaryRelation.TYPE_ID) {
                            expression = t.tokenStr;
                        }
                    } else if (i+1 == tokens.length) {
                        if (i-1 >= 0) {
                            if (tokens[i-1].tokenTypeId != BinaryRelation.TYPE_ID) {
                                expression = t.tokenStr;
                            }
                        } else {
                            expression = t.tokenStr;
                        }
                    }
                } else if (t.tokenTypeId == BinaryRelation.TYPE_ID) {
                    if ((i+1 < tokens.length) && (i-1 >= 0)) {
                        if ((tokens[i-1].tokenTypeId == 0) || tokens[i+1].tokenTypeId == 0) {
                            if (tokens[i-1].looksLike.equals("argument") || tokens[i+1].looksLike.equals("argument")) {
                                expression = tokens[i-1].tokenStr + t.tokenStr + tokens[i+1].tokenStr;
                            }
                        }
                    }
                }
                i++;
            }
        }
        return expression;
    }

}
