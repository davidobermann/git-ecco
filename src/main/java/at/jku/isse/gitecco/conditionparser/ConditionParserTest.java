package at.jku.isse.gitecco.conditionparser;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;
import org.mariuszgromada.math.mxparser.parsertokens.BinaryRelation;
import org.mariuszgromada.math.mxparser.parsertokens.ParserSymbol;
import org.mariuszgromada.math.mxparser.parsertokens.Token;

public class ConditionParserTest {

    public static void main(String args[]) {

        String exp = "!((FOO3 || (100 < BAR)) && FOO1 && FOO2)";
        //String exp = "ENABLED(I2C_EEPROM) || ENABLED(SPI_EEPROM)";
        //String exp = "";
        Expression e = new Expression(exp);

        System.out.println("Expression");
        System.out.println(e.getExpressionString());

        System.out.println("-------------");

        System.out.println("Print all Arguments:");
        e.getCopyOfInitialTokens()
                .stream()
                .filter(x -> x.looksLike == "argument")
                .forEach(x -> System.out.println(x.tokenStr));

        System.out.println("-------------");

        System.out.println("Tokentype:\n");
        Token[] tokens = e.getCopyOfInitialTokens().toArray(new Token[e.getCopyOfInitialTokens().size()]);
        for (Token token : tokens) {
            //System.out.println(token.tokenTypeId);
            String s = mXparser.getTokenTypeDescription(token.tokenTypeId);
            System.out.println(s != "" ? s : token.looksLike);
        }

        System.out.println("-------------");

        String s = "";
        int lvlbuf = 0;
        int i = 0;
        for (Token token : tokens) {
            if (token.tokenLevel == lvlbuf) {
                s += (token.tokenTypeId == ParserSymbol.TYPE_ID)
                        || (token.looksLike.equals("function"))
                        || (token.tokenTypeId == 1) ? "" : token.tokenStr;
                if (i == tokens.length-1) {
                    if (s.length()>0) System.out.println(s);
                    parseLeveledExperssion(s, token);
                }
            } else {
                if (s.length()>0) System.out.println(s);
                parseLeveledExperssion(s, token);
                lvlbuf = token.tokenLevel;
                s = (token.tokenTypeId == ParserSymbol.TYPE_ID) ? "" : token.tokenStr;// || (token.tokenTypeId == 1)
            }
            i++;
        }
    }

    private static String parseLeveledExperssionWithDefines(String expression, Token token) {
        int i = 0;
        double offset = 0;

        if (expression.length()>0) {
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
                    } else if ((i-1 >= 0) && (i+1<tokens.length)) {
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
                    if ((i+1<tokens.length) && (i-1 >= 0)) {
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
        }
        return expression;
    }

    private static String parseLeveledExperssion(String expression, Token token) {
        int i = 0;

        if (expression.length()>0) {
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
                    } else if ((i-1 >= 0) && (i+1<tokens.length)) {
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
                    if ((i+1<tokens.length) && (i-1 >= 0)) {
                        if ((tokens[i-1].tokenTypeId == 0) || tokens[i+1].tokenTypeId == 0) {
                            if (tokens[i-1].looksLike.equals("argument") || tokens[i+1].looksLike.equals("argument")) {
                                System.out.println(tokens[i-1].tokenStr+t.tokenStr+tokens[i+1].tokenStr+" must be defined");
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
