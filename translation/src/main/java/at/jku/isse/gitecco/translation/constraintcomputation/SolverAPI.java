package at.jku.isse.gitecco.translation.constraintcomputation;

import at.jku.isse.gitecco.translation.preprocessor.PreprocessorHelper;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.Token;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.featureExpr.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;

import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.expression.discrete.relational.ReExpression;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.Arrays;
import java.util.Stack;

import static org.chocosolver.solver.constraints.nary.cnf.LogOp.*;

public class SolverAPI {

    public static void main(String... args) {
        testChoco();
        //testPP();
        testFeatureExpParser();
        solve();
    }

    private static void testPP() {
        PreprocessorHelper pph = new PreprocessorHelper();
        //TODO: Test
        //pph.generateCleanVersion();
    }

    private static void testFeatureExpParser() {
        //String expr = "!A && B || C <= 100 || (C > 100 ? A : B)";
        //String expr = "!a&&b||a&&!b||c<10";
        String expr = "!a&&b||a&&!b||c<10";

        FeatureExpressionParser p = new FeatureExpressionParser(expr);
        FeatureExpression root = p.parse();

        traverse(root);
    }

    private static boolean isIntVar = false;
    private final static Model model = new Model("auto");
    private final static Stack<Variable> stack = new Stack<>();

    private static void traverse(FeatureExpression expr) {
        if(expr == null) return;

        String name = "";

        if(expr instanceof Name) {
            operation(expr); //make Var
            //TODO: call check vars for name if check returns var push this one else --> create new
            if(isIntVar) {
                stack.push(model.intVar(((Name) expr).getToken().getText(),Short.MIN_VALUE,Short.MAX_VALUE));
            } else {
                stack.push(model.boolVar(((Name) expr).getToken().getText()));
            }
        } else if(expr instanceof NumberLiteral) {
            operation(expr); //make fixed value --> constant
            stack.push(model.intVar(Integer.valueOf((((NumberLiteral) expr).getToken().getText()))));
        } else if(expr instanceof SingleTokenExpr) {
            operation(expr); //make operator
            IntVar right = null;
            IntVar left = null;
            BoolVar bright = null;
            BoolVar bleft = null;
            SingleTokenExpr e = (SingleTokenExpr) expr;
            switch (e.getToken().getType()) {
                case Token.GE:      //greater than or equal ">="
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.ge(right).boolVar());
                    break;
                case Token.EQ:      //equal "=="
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.eq(right).boolVar());
                    break;
                case Token.LE:      //less than or equal "<="
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.le(right).boolVar());
                    break;
                case Token.LOR:     //logical or "||"
                    bright = stack.pop().asBoolVar();
                    bleft = stack.pop().asBoolVar();
                    stack.push(bleft.or(bright).boolVar());
                    break;
                case Token.LAND:    //logical and "&&
                    bright = stack.pop().asBoolVar();
                    bleft = stack.pop().asBoolVar();
                    stack.push(bleft.or(bright).boolVar());
                    break;
                case Token.NE:      //not equal "!="
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.ne(right).boolVar());
                    break;
                case 60:            //less than "<"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.lt(right).boolVar());
                    break;
                case 62:            //greater than ">"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.gt(right).boolVar());
                    break;
                case 33:            //not "!"
                    break;
                case 43:            //plus "+"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.add(right).intVar());
                    break;
                case 45:            //minus "-"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.sub(right).intVar());
                    break;
                case 42:            //mul "*"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.mul(right).intVar());
                    break;
                case 47:            //div "/"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.div(right).intVar());
                    break;
                case 37:            //modulo "%"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.mod(right).intVar());
                    break;
                case 94:            //pow "^"
                    right = stack.pop().asIntVar();
                    left = stack.pop().asIntVar();
                    stack.push(left.pow(right).intVar());
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

    private static void solve() {
        model.post(stack.pop().asBoolVar().extension());
        Solution solution = model.getSolver().findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        }
    }

    private Variable checkVars(String name) {
        for (Variable var : model.getVars()) {
            if(var.getName().equals(name)) return var;
        }
        return null;
    }

    private static void operation(FeatureExpression expr) {
        //System.out.println(expr.toString());
    }


    private static void testChoco() {
        //BoolVar a = model.boolVar("a");
        //BoolVar b = model.boolVar("b");
        //BoolVar c = model.boolVar("c");
        //IntVar num = model.intVar(10);
        //IntVar x = model.intVar(Short.MIN_VALUE, Short.MAX_VALUE);
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

        //BoolVar a = model.boolVar("a");
        //BoolVar b = model.boolVar("b");
        //IntVar  c = model.intVar("c", Short.MIN_VALUE, Short.MAX_VALUE);
        //BoolVar a = model.intVar("a",Short.MIN_VALUE,Short.MAX_VALUE);
        //BoolVar b = model.intVar("b",Short.MIN_VALUE,Short.MAX_VALUE).asBoolVar();

        //Constraint c1 = model.and(a.not(), b);
        //Constraint c2 = model.and(b.not(), a);
        //Constraint c3 = model.or(c.intVar().gt(10).boolVar());
        //Constraint constraint = model.or(c1,c2,c3);

        //IntVar f = model.boolVar("f");
        //IntVar val = model.intVar(10);

        //Constraint constraint = model.and(c.gt(val).boolVar(), f.asBoolVar().not(), a.asBoolVar());

        Variable x = model.boolVar("x");
        Variable x1 = model.boolVar("x2", true);
        Variable zehn = model.intVar(10);
        Variable y = model.intVar("y", Short.MIN_VALUE, Short.MAX_VALUE);

        for (Variable var : model.getVars()) {
            System.out.println(var.getName().equals("x"));
        }

        Variable zw = y.asIntVar().eq(zehn.asIntVar()).boolVar();
                //y.asIntVar().lt(zehn.asIntVar()).boolVar();

        Variable erg = zw.asBoolVar().eq(zw.asBoolVar()).boolVar();
                //x.asBoolVar().and(zw.asBoolVar()).boolVar();

        zw = x.asBoolVar().eq(x1.asBoolVar()).boolVar();

        model.post(zw.asBoolVar().extension());

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
