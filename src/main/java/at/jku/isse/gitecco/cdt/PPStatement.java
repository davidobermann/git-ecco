package at.jku.isse.gitecco.cdt;

import org.eclipse.cdt.core.dom.ast.*;

/**
 * Class to store Eclipse CDT preprocessor statements of certain types.
 */
public class PPStatement {
    private final IASTPreprocessorStatement statement;
    private final int lineStart;
    private final int lineEnd;

    /**
     * Creates a new PPStatement
     *
     * @param pps IASTPreprocessorStatement which should be checked.
     * @throws Exception - If the IASTPreprocessorStatement is not of the right type.
     */
    public PPStatement(IASTPreprocessorStatement pps) throws Exception {

        if (!(pps instanceof IASTPreprocessorElifStatement
                || pps instanceof IASTPreprocessorIfdefStatement
                || pps instanceof IASTPreprocessorIfStatement
                || pps instanceof IASTPreprocessorElseStatement
                || pps instanceof IASTPreprocessorIfndefStatement
                || pps instanceof IASTPreprocessorEndifStatement)) {
            throw new Exception("not the right preprocessor statement");
        }

        this.lineStart = pps.getFileLocation().getStartingLineNumber();
        this.lineEnd = pps.getFileLocation().getEndingLineNumber();
        this.statement = pps;
    }

    /**
     * Gets the ending line number of the PPStatement
     *
     * @return the ending line number
     */
    public int getLineEnd() {
        return lineEnd;
    }

    /**
     * Gets the starting line number of the PPStatement
     *
     * @return The starting line number of the PPStatement
     */
    public int getLineStart() {
        return lineStart;
    }

    /**
     * Gets the IASTPreprocessorStatement of which this PPStatement was created
     *
     * @return The IASTPreprocessorStatement of which this PPStatement was created
     */
    public IASTPreprocessorStatement getStatement() {
        return this.statement;
    }
}

