package at.jku.isse.gitecco.translation.constraintcomputation;

import at.jku.isse.gitecco.translation.preprocessor.PreprocessorHelper;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.Token;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.featureExpr.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;

import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.Stack;

import static org.chocosolver.solver.constraints.nary.cnf.LogOp.*;

public class SolverAPI {

    public static void main(String... args) {
        testChoco();
        //testPP();
        testFeatureExpParser();
    }

    private static void testPP() {
        PreprocessorHelper pph = new PreprocessorHelper();
        //TODO: Test
        //pph.generateCleanVersion();
    }

    private static void testFeatureExpParser() {
        //String expr = "!A && B || C <= 100 || (C > 100 ? A : B)";
        //String expr = "!a&&b||a&&!b||c<10";
        String expr = "!a&&b||a&&!b||c+2<10";

        FeatureExpressionParser p = new FeatureExpressionParser(expr);
        FeatureExpression root = p.parse();

        traverse(root);
    }

    private static boolean isIntVar = false;
    private Constraint constraint = null;
    private final static Model model = new Model("auto");
    private final static Stack<IntVar> stack = new Stack<>();
    private static LogOp logop = null;

    private static void traverse(FeatureExpression expr) {
        if(expr == null) return;

        if(expr instanceof Name) {
            operation(expr); //make Var
            if(isIntVar) {
                stack.push(model.intVar(((NumberLiteral) expr).getToken().getText(),Short.MIN_VALUE,Short.MAX_VALUE));
            } else {
                stack.push(model.boolVar(((Name) expr).getToken().getText()));
            }

        } else if(expr instanceof NumberLiteral) {
            operation(expr); //make fixed value --> constant
            stack.push(model.intVar(Integer.valueOf((((NumberLiteral) expr).getToken().getText()))));
        } else if(expr instanceof SingleTokenExpr) {
            operation(expr); //make operator

            SingleTokenExpr e = (SingleTokenExpr) expr;
            switch (e.getToken().getType()) {
                case Token.GE:      //greater than or equal ">="
                    //popleft
                    //popright
                    //ge logop or constraint
                    break;
                case Token.EQ:      //equal "=="
                    break;
                case Token.LE:      //less than or equal "<="
                    break;
                case Token.LOR:     //logical or "||"
                    break;
                case Token.LAND:    //logical and "&&"
                    break;
                case Token.NE:      //not equal "!="
                    break;
                case 60:            //less than "<"
                    break;
                case 62:            //greater than ">"
                    break;
                case 33:            //not "!"
                    break;
                case 43:            //plus "+"
                    break;
                case 45:            //minus "-"
                    break;
                case 42:            //mul "*"
                    break;
                case 47:            //div "/"
                    break;
                case 37:            //modulo "%"
                    break;
                case 94:            //pow "^"
                    break;
                default:
                    System.err.println("unexpected token with token id: " + e.getToken().getType() + " and symbol: " + e.getToken().getText());
                    break;
            }
        } else if(expr instanceof CondExpr) {
            CondExpr e = (CondExpr) expr;
            //idea: parse that created expression and attach it instead of the CondExpr and continue to traverse again.
            String cond = "(!("+e.getExpr()+")||("+e.getThenExpr()+"))&&(("+e.getExpr()+")||("+e.getElseExpr() + "))";
            traverse(new FeatureExpressionParser(cond).parse());
        } else if(expr instanceof PrefixExpr) {
            traverse(((PrefixExpr) expr).getExpr());
            traverse(((PrefixExpr) expr).getOperator());
        } else if(expr instanceof InfixExpr) {
            int op = ((InfixExpr) expr).getOperator().getToken().getType();

            if(op == 43 || op == 45 || op == 42 || op == 47 || op == 37 //266 == wirklich notwendig?
                    || op == 94 || op == 267 || op == 266 || op == 283 || op == 60 || op == 62 || op == 275)
                isIntVar = true;
            else isIntVar = false;

            traverse(((InfixExpr) expr).getLeftHandSide());
            traverse(((InfixExpr) expr).getRightHandSide());
            traverse(((InfixExpr) expr).getOperator());
        } else if(expr instanceof ParenthesizedExpr){
            traverse(((ParenthesizedExpr) expr).getExpr());
        } else {
            operation(expr);
            System.err.println("unexpected node in AST: " + expr.getClass());
        }
    }

    private static void operation(FeatureExpression expr) {
        System.out.println(expr.toString());
    }


    private static void testChoco() {
        //BoolVar a = model.boolVar("a");
        //BoolVar b = model.boolVar("b");
        //BoolVar c = model.boolVar("c");
        //IntVar num = model.intVar(10);
        IntVar x = model.intVar(Short.MIN_VALUE, Short.MAX_VALUE);
        //model.and(a,b);
        //model.addClauses(and(and(and(a,c),and(b.not(),c)),x.lt(10).boolVar()));
        //model.addClauseTrue(x.lt(10).boolVar());
        //model.addClauseTrue(x.gt(5).boolVar());
        //model.addClauseTrue(a.not());

        Model model = new Model("experiment");
        //test: #ifdef !a&&b||a&&!b||c<10

        /*#ifdef !a&&b||a&&!b||c<10
            int i;
            i++;
        #endif

                a, !b, c=5*/

        BoolVar a = model.boolVar("a");
        //BoolVar b = model.boolVar("b");
        IntVar  c = model.intVar("c", Short.MIN_VALUE, Short.MAX_VALUE);
        //BoolVar a = model.intVar("a",Short.MIN_VALUE,Short.MAX_VALUE);
        //BoolVar b = model.intVar("b",Short.MIN_VALUE,Short.MAX_VALUE).asBoolVar();

        //Constraint c1 = model.and(a.not(), b);
        //Constraint c2 = model.and(b.not(), a);
        //Constraint c3 = model.or(c.intVar().gt(10).boolVar());
        //Constraint constraint = model.or(c1,c2,c3);

        IntVar f = model.boolVar("f");
        IntVar val = model.intVar(10);

        Constraint constraint = model.and(c.gt(val).boolVar(), f.asBoolVar().not(), a.asBoolVar());

        model.post(constraint);

        Solution solution = model.getSolver().findSolution();

        //System.out.println(b.getBooleanValue());

        if (solution != null) {
            System.out.println(solution.toString());
        }

        /*
         * Idea of solving:
         *  - create variables
         *  - create constraints
         *  - post constraint to model
         *  - extract solution for created variables
         *
         * Possible challenges:
         *  - variable names
         *  - decomposing the formula correctly
         */

    }
}
