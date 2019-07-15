package at.jku.isse.gitecco.core.solver;

import at.jku.isse.gitecco.core.type.Feature;
import org.anarres.cpp.Token;
import org.anarres.cpp.featureExpr.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.*;

/**
 * Class for finding positive solutions for preprocessor conditions
 */
public class ExpressionSolver {
	private String expr;
	private Model model;
	private final List<IntVar> vars;
	private final Stack<Variable> stack;
	private boolean isIntVar = false;
	private final List<FeatureImplication> implications;

	/**
	 * Inner Class for handling feature implications
	 */
	private class FeatureImplication {
		BoolVar a;
		Constraint c;

		public FeatureImplication(BoolVar a, Constraint c) {
			this.a = a;
			this.c = c;
		}
	}

	/**
	 * Create new solver with a given expression to solve.
	 *
	 * @param expr
	 */
	public ExpressionSolver(String expr) {
		this.expr = expr;
		this.model = new Model();
		this.vars = new LinkedList<>();
		this.stack = new Stack<>();
		this.implications = new LinkedList<>();
	}

	/**
	 * Resets the solver so a new expression can be solved.
	 */
	public void reset() {
		this.expr = "";
		this.model = new Model();
		this.vars.clear();
		this.stack.clear();
		this.implications.clear();
	}

	/**
	 * Resets just the implications
	 */
	public void clearImpications() {
		this.implications.clear();
	}

	/**
	 * Resets the solver and assigns a new expression to solve.
	 *
	 * @param expr
	 */
	public void reset(String expr) {
		this.expr = expr;
		this.model = new Model();
		this.vars.clear();
		this.stack.clear();
		this.implications.clear();
	}

	/**
	 * Sets a new expression for the solver.
	 *
	 * @param expr
	 */
	public void setExpr(String expr) {
		this.expr = expr;
	}

	/**
	 * Solves the expression currently assigned to this solver.
	 * Returns a Map with the Feature as key and the value to be assigned as an Integer.
	 *
	 * @return The Map with the solution.
	 */
	public Map<Feature, Integer> solve() {
		Map<Feature, Integer> assignments = new HashMap<>();

		//parse the expression and traverse the syntax tree
		FeatureExpressionParser p = new FeatureExpressionParser(expr);
		FeatureExpression root = p.parse();
		traverse(root);

		//add all the feature implications to the model:
		Variable ifVar = null;
		Variable thenVar = null;

		for (FeatureImplication im : implications) {
			ifVar = checkVars(model, im.a.getName());
			//TODO: ...
			if (ifVar == null) ifVar = model.boolVar(im.a.getName());
//			if (thenVar == null) thenVar = model.boolVar(im.b.getName());

			this.vars.add(ifVar.asIntVar());

			//model.ifThen(ifVar.asBoolVar(),thenVar.asBoolVar().extension());
			model.ifThenElse(ifVar.asBoolVar(), thenVar.asBoolVar().extension(), thenVar.asBoolVar().not().extension());
		}

		//add the parsed problem to the solver model
		model.post(stack.pop().asBoolVar().extension());

		//acutal solving
		Solution solution = model.getSolver().findSolution();
		if (solution != null) {
			for (IntVar var : vars) {
				assignments.put(new Feature(var.getName()), solution.getIntVal(var));
			}
		} else {
			System.err.println("DEAD CODE: No solution found for " + expr);
		}

		return Collections.unmodifiableMap(assignments);
	}


	public void addFeatureImplication(String ifex, String thenex) {
		//TODO: how to react to implicated assignments to variables?
		FeatureExpressionParser p = new FeatureExpressionParser(ifex);
		FeatureExpression root = p.parse();
		traverse(root);
		BoolVar bif = stack.pop().asBoolVar();

		p = new FeatureExpressionParser(thenex);
		root = p.parse();

		if (checkForAssignment(root)) {
			if (root instanceof AssignExpr) {
				FeatureExpression left = ((AssignExpr) root).getLeftHandSide();
				FeatureExpression right = ((AssignExpr) root).getRightHandSide();
				if (left instanceof Name) {
					//TODO: left side of numeric implication
				} else {
					//TODO: right side of implication.
				}
			}
		} else {
			traverse(root);
			BoolVar bthen = stack.pop().asBoolVar();
			this.implications.add(new FeatureImplication(bif, bthen.extension()));
		}
	}

	private boolean checkForAssignment(FeatureExpression expr) {
		if (expr == null) return false;

		if (expr instanceof AssignExpr) {
			return true;
		} else if (expr instanceof CondExpr) {
			CondExpr e = (CondExpr) expr;
			//idea: parse that created expression and attach it instead of the CondExpr and continue to traverse again.
			String cond = "(!(" + e.getExpr() + ")||(" + e.getThenExpr() + "))&&((" + e.getExpr() + ")||(" + e.getElseExpr() + "))";
			traverse(new FeatureExpressionParser(cond).parse());
		} else if (expr instanceof PrefixExpr) {
			traverse(((PrefixExpr) expr).getExpr());
			traverse(((PrefixExpr) expr).getOperator());
		} else if (expr instanceof InfixExpr) {
			traverse(((InfixExpr) expr).getLeftHandSide());
			traverse(((InfixExpr) expr).getRightHandSide());
			traverse(((InfixExpr) expr).getOperator());
		} else if (expr instanceof ParenthesizedExpr) {
			traverse(((ParenthesizedExpr) expr).getExpr());
		}
		return false;
	}

	/**
	 * Helper Method
	 * Traverses the expression tree which was created before by the FeatureExpressionParser.
	 *
	 * @param expr the expression tree to be parsed.
	 */
	private void traverse(FeatureExpression expr) {
		if (expr == null) return;

		if (expr instanceof Name) {
			String name = ((Name) expr).getToken().getText();
			Variable check = checkVars(model, name);
			if (check == null) {
				if (isIntVar) {
					IntVar iv = model.intVar(name, Short.MIN_VALUE, Short.MAX_VALUE);
					vars.add(iv);
					stack.push(iv);
				} else {
					BoolVar bv = model.boolVar(name);
					vars.add(bv);
					stack.push(bv);
				}
			} else {
				stack.push(check);
			}
		} else if (expr instanceof AssignExpr) {
			//TODO: does not work like this.
			//TODO: need to push a BoolVar, but that does not work --> instantiate the intVar somehow conditional
			AssignExpr aexp = (AssignExpr) expr;
			if (aexp.getLeftHandSide() instanceof Name) {
				String name = ((Name) aexp.getLeftHandSide()).getToken().getText();
				Variable check = checkVars(model, name);
				if (check == null) {
					IntVar iv = model.intVar(name, Double.valueOf((((NumberLiteral) aexp.getRightHandSide()).getToken().getText())).intValue());
					vars.add(iv);
					//TODO: !! not like this !!
					stack.push(model.boolVar(true));
				} else {
					try {
						check.asIntVar().instantiateTo(Double.valueOf((((NumberLiteral) aexp.getRightHandSide()).getToken().getText())).intValue(), null);
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			} else {
				String name = ((Name) aexp.getRightHandSide()).getToken().getText();
				Variable check = checkVars(model, name);
				if (check == null) {
					IntVar iv = model.intVar(name, Double.valueOf((((NumberLiteral) aexp.getLeftHandSide()).getToken().getText())).intValue());
					vars.add(iv);
					stack.push(model.boolVar(true));
				} else {
					try {
						check.asIntVar().instantiateTo(Double.valueOf((((NumberLiteral) aexp.getLeftHandSide()).getToken().getText())).intValue(), null);
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		} else if (expr instanceof NumberLiteral) {
			stack.push(model.intVar(Double.valueOf((((NumberLiteral) expr).getToken().getText())).intValue()));
			isIntVar = true;
		} else if (expr instanceof SingleTokenExpr) {
			IntVar right;
			IntVar left;
			BoolVar bright;
			BoolVar bleft;
			SingleTokenExpr e = (SingleTokenExpr) expr;
			switch (e.getToken().getType()) {
				case Token.GE:      //greater than or equal ">="
					right = stack.pop().asIntVar();
					left = stack.pop().asIntVar();
					stack.push(left.ge(right).boolVar());
					break;
				case Token.EQ:      //equal "=="
					if (isIntVar) {
						right = stack.pop().asIntVar();
						left = stack.pop().asIntVar();
						stack.push(left.eq(right).boolVar());
					} else {
						bright = stack.pop().asBoolVar();
						bleft = stack.pop().asBoolVar();
						stack.push(bleft.eq(bright).boolVar());
					}
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
					stack.push(bleft.and(bright).boolVar());
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
					bright = stack.pop().asBoolVar();
					stack.push(bright.not());
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
		} else if (expr instanceof CondExpr) {
			CondExpr e = (CondExpr) expr;
			//idea: parse that created expression and attach it instead of the CondExpr and continue to traverse again.
			String cond = "(!(" + e.getExpr() + ")||(" + e.getThenExpr() + "))&&((" + e.getExpr() + ")||(" + e.getElseExpr() + "))";
			traverse(new FeatureExpressionParser(cond).parse());
		} else if (expr instanceof PrefixExpr) {
			traverse(((PrefixExpr) expr).getExpr());
			traverse(((PrefixExpr) expr).getOperator());
		} else if (expr instanceof InfixExpr) {
			int op = ((InfixExpr) expr).getOperator().getToken().getType();

			if (op == 43 || op == 45 || op == 42 || op == 47 || op == 37 //266 == wirklich notwendig?
					|| op == 94 || op == 267 || op == 266 || op == 283 || op == 60 || op == 62 || op == 275)
				isIntVar = true;
			else isIntVar = false;

			traverse(((InfixExpr) expr).getLeftHandSide());
			traverse(((InfixExpr) expr).getRightHandSide());
			traverse(((InfixExpr) expr).getOperator());
		} else if (expr instanceof ParenthesizedExpr) {
			traverse(((ParenthesizedExpr) expr).getExpr());
		} else {
			System.err.println("unexpected node in AST: " + expr.getClass());
		}
	}

	/**
	 * Helper Method
	 * Checks if a variable with a given name exists already in a given model.
	 * If it does the variable is returned. Otherwise null is returned.
	 *
	 * @param model
	 * @param name
	 * @return
	 */
	private Variable checkVars(Model model, String name) {
		for (Variable var : model.getVars()) {
			if (var.getName().equals(name)) return var;
		}
		return null;
	}


}
