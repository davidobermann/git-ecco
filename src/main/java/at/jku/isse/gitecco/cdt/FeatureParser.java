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
     * Gets all the features contained in a file of c++ code.
     * <p>
     * !!Until now the code is awful, but it works.!!
     * !!When there is time, the code structure should be improved dramatically.!!
     *
     * @param ppstatements all the Preprocessor Statements of a Eclipse CDT C++ AST.
     * @return Array of al Features found in the preprocessor statements.
     */
    public Feature[] parse(IASTPreprocessorStatement[] ppstatements, int linecnt) {
        final List<Feature> features = new ArrayList<>();
        final Stack<IASTPreprocessorStatement> stack = new Stack<IASTPreprocessorStatement>();
        boolean elseFlag = false;
        PPStatement pp;

        features.add(new Feature(0, linecnt, "BASE"));

        for (IASTPreprocessorStatement pps : ppstatements) {
            try {

                if (CDTHelper.isFeatureStart(pps)) {
                    stack.push(pps);
                } else if (pps instanceof IASTPreprocessorElifStatement) {

                    if (stack.empty()) {
                        throw new Exception("wrong definition of features");
                    } else if (CDTHelper.isFeatureStart(stack.peek())
                            || (stack.peek() instanceof IASTPreprocessorElifStatement)) {

                        pp = new PPStatement(stack.pop());
                        String condName = CDTHelper.getCondName(pp.getStatement());
                        String[] fnames = ConditionParser.parseCondition(condName);
                        for (String fname : fnames) {
                            features.add(new Feature(pp.getLineStart(), pps.getFileLocation().getEndingLineNumber(), fname));
                        }
                    } else {
                        throw new Exception("wrong definition of features");
                    }
                    stack.push(pps);

                } else if (pps instanceof IASTPreprocessorElseStatement) {

                    if (stack.empty()) {
                        throw new Exception("wrong definition of features");
                    } else if (CDTHelper.isFeatureStart(stack.peek())
                            || (stack.peek() instanceof IASTPreprocessorElifStatement)) {

                        elseFlag = true;
                        pp = new PPStatement(stack.pop());
                        String condName = CDTHelper.getCondName(pp.getStatement());
                        String[] fnames = ConditionParser.parseCondition(condName);
                        for (String fname : fnames) {
                            features.add(new Feature(pp.getLineStart(), pps.getFileLocation().getEndingLineNumber(), fname));

                        }
                    } else {
                        throw new Exception("wrong definition of features");
                    }

                } else if ((pps instanceof IASTPreprocessorEndifStatement)) {
                    if (elseFlag) {
                        elseFlag = false;
                    } else {
                        if (stack.empty()) {
                            throw new Exception("wrong definition of features");
                        } else if (CDTHelper.isFeatureStart(stack.peek())
                                || (stack.peek() instanceof IASTPreprocessorElifStatement)) {

                            pp = new PPStatement(stack.pop());
                            String condName = CDTHelper.getCondName(pp.getStatement());
                            String[] fnames = ConditionParser.parseCondition(condName);
                            for (String fname : fnames) {
                                features.add(new Feature(pp.getLineStart(), pps.getFileLocation().getEndingLineNumber(), fname));

                            }
                        } else {
                            throw new Exception("wrong definition of features");
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        Collections.sort(features);
        return features.toArray(new Feature[features.size()]);
    }



    public TreeFeature parseToTree(IASTPreprocessorStatement[] ppstatements, int linecnt) throws Exception {
        final TreeFeature root = new TreeFeature(new Feature(0, linecnt, "BASE"));
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
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), fnames));
            }else if(pps instanceof IASTPreprocessorElifStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                String[] fnames = ConditionParser.parseCondition(condName);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), fnames));
            } else if(pps instanceof IASTPreprocessorElseStatement) {
                pp = new PPStatement(pps);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                elseFlag = true;
            } else if(pps instanceof IASTPreprocessorEndifStatement) {
                if(!elseFlag){
                    pp = new PPStatement(pps);
                    currentNode.setEndingLineNumber(pp.getLineEnd());
                    currentNode = currentNode.getParent();
                } else {
                    elseFlag = false;
                }
            }
        }

        return root;
    }

}
