package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.conditionparser.ConditionParser;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;

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

        features.add(new Feature("BASE", 0, linecnt));

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
                            features.add(new Feature(fname, pp.getLineStart(), pps.getFileLocation().getEndingLineNumber()));
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
                            features.add(new Feature(fname, pp.getLineStart(), pps.getFileLocation().getEndingLineNumber()));
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
                                features.add(new Feature(fname, pp.getLineStart(), pps.getFileLocation().getEndingLineNumber()));
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



    public TreeFeature parse(Feature[] features, int linecnt, boolean b) {
        final TreeFeature root = new TreeFeature(new Feature("BASE", 0, linecnt));
        final Stack<Feature> stack = new Stack<>();
        TreeFeature currentNode = root;
        Feature curElem;

        for (int i = 0; i < features.length-1; i++) {
            features[i].contains(features[i+1]);
            if(features[i].contains(features[i+1])){
                stack.push(features[i]);
                currentNode = currentNode.addChild(features[i]);
            } else {
                while(!stack.empty() && !stack.peek().contains(features[i]))
                    stack.pop();
            }
        }

        return root;
    }


}
