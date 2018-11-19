package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.conditionparser.ConditionParser;
import at.jku.isse.gitecco.conditionparser.ParsedCondition;
import org.eclipse.cdt.core.dom.ast.*;

/**
 * Class for parsing features in the c++ file.
 */
public class FeatureParser {

    /**
     * Retains all the features contained in the file as a tree of features --> TreeFeature
     *
     * @param ppstatements
     * @param linecnt
     * @return The root of the tree.
     * @throws Exception
     */
    public TreeFeature parseToTreeDefNew(IASTPreprocessorStatement[] ppstatements, int linecnt) throws Exception {
        final TreeFeature root = new TreeFeature(new Feature(0, linecnt, FeatureType.IF, new ParsedCondition("BASE", 1)));
        TreeFeature currentNode = root;
        PPStatement pp;

        for (IASTPreprocessorStatement pps : ppstatements) {
            if (pps instanceof IASTPreprocessorIfStatement
                    || pps instanceof IASTPreprocessorIfdefStatement
                    || pps instanceof IASTPreprocessorIfndefStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                ParsedCondition[] pc = ConditionParser.parseConditionWithDefition(condName);
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.IF, pc));
            } else if (pps instanceof IASTPreprocessorElifStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                ParsedCondition[] pc = ConditionParser.parseConditionWithDefition(condName);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELIF, pc));
            } else if (pps instanceof IASTPreprocessorElseStatement) {
                pp = new PPStatement(pps);
                ParsedCondition[] pc = currentNode.getParent().getConditions();
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELSE, pc));
            } else if (pps instanceof IASTPreprocessorEndifStatement) {
                pp = new PPStatement(pps);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
            }
        }

        return root;
    }

    /**
     * Retains all the features contained in the file as a tree of features --> TreeFeature
     *
     * @param ppstatements
     * @param linecnt
     * @return The root of the tree.
     * @throws Exception
     */
    public TreeFeature parseToTreeDef(IASTPreprocessorStatement[] ppstatements, int linecnt) throws Exception {
        final TreeFeature root = new TreeFeature(new Feature(0, linecnt, FeatureType.IF, new ParsedCondition("BASE", 1)));
        TreeFeature currentNode = root;
        PPStatement pp;

        for (IASTPreprocessorStatement pps : ppstatements) {
            if (pps instanceof IASTPreprocessorIfStatement
                    || pps instanceof IASTPreprocessorIfdefStatement
                    || pps instanceof IASTPreprocessorIfndefStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                ParsedCondition[] pc = ConditionParser.parseConditionWithDefition(condName);
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.IF, pc));
            } else if (pps instanceof IASTPreprocessorElifStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                ParsedCondition[] pc = ConditionParser.parseConditionWithDefition(condName);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELIF, pc));
            } else if (pps instanceof IASTPreprocessorElseStatement) {
                pp = new PPStatement(pps);
                ParsedCondition[] pc = currentNode.getParent().getConditions();
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELSE, pc));
            } else if (pps instanceof IASTPreprocessorEndifStatement) {
                pp = new PPStatement(pps);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
            }
        }

        return root;
    }

}
