package at.jku.isse.gitecco.core.conditionparser;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PseudoBooleanParser;

import java.util.ArrayList;
import java.util.List;

public class ConditionParser {
    public static List<Feature> parseCondition(String cond) {
        final List<Feature> ret = new ArrayList<>();

        try {
            final FormulaFactory f = new FormulaFactory();
            final PseudoBooleanParser p = new PseudoBooleanParser(f);
            cond = cond.replace('!', '~').replace("&&", "&").replace("||", "|");
            final Formula formula = p.parse(cond);
            for (Literal literal : formula.literals()) {
                ret.add(new Feature(literal.name()));
            }

        } catch (ParserException e) {
            System.out.println("Error while extracting literals of condition");
            e.printStackTrace();
        }

        return ret;
    }

    public static List<Feature> parseConditionNew(String cond) {


        return null;
    }
}
