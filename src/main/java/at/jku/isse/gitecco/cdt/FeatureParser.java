package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.conditionparser.ConditionParser;
import org.eclipse.cdt.core.dom.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

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
    public TreeFeature parseToTree(IASTPreprocessorStatement[] ppstatements, int linecnt) throws Exception {
        final TreeFeature root = new TreeFeature(new Feature(0, linecnt, FeatureType.IF, "BASE"));
        TreeFeature currentNode = root;
        boolean elseFlag = false;
        PPStatement pp;

        for(IASTPreprocessorStatement pps : ppstatements) {
            if(pps instanceof IASTPreprocessorIfStatement
                    || pps instanceof  IASTPreprocessorIfdefStatement
                    || pps instanceof  IASTPreprocessorIfndefStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                String[] fnames = ConditionParser.parseCondition(condName);
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.IF, fnames));
            }else if(pps instanceof IASTPreprocessorElifStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                String[] fnames = ConditionParser.parseCondition(condName);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELIF, fnames));
            } else if(pps instanceof IASTPreprocessorElseStatement) {
                /*pp = new PPStatement(pps);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                elseFlag = true;*/
                pp = new PPStatement(pps);
                String[] fnames = currentNode.getParent().getName();
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELSE, fnames));
            } else if(pps instanceof IASTPreprocessorEndifStatement) {
                /*if(!elseFlag){
                    pp = new PPStatement(pps);
                    currentNode.setEndingLineNumber(pp.getLineEnd());
                    currentNode = currentNode.getParent();
                } else {
                    elseFlag = false;
                }*/
                pp = new PPStatement(pps);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
            }
        }

        return root;
    }

}
