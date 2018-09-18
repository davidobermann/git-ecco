package at.jku.isse.gitecco.cdt;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;

import java.util.Optional;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

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
    public static Feature[] parse(IASTPreprocessorStatement[] ppstatements) {
        final SortedSet<Feature> features = new TreeSet<Feature>();
        final Stack<IASTPreprocessorStatement> stack = new Stack<IASTPreprocessorStatement>();
        boolean elseFlag = false;
        PPStatement pp;

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
                        features.add(new Feature(condName, Optional.ofNullable(null),
                                pp.getLineStart()-1, pps.getFileLocation().getEndingLineNumber()-1));
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
                        features.add(new Feature(condName, Optional.ofNullable(null),
                                pp.getLineStart()-1, pps.getFileLocation().getEndingLineNumber()-1));
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
                            features.add(new Feature(condName, Optional.ofNullable(null),
                                    pp.getLineStart()-1, pps.getFileLocation().getEndingLineNumber()-1));
                        } else {
                            throw new Exception("wrong definition of features");
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return features.toArray(new Feature[features.size()]);
    }
}
