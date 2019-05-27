package at.jku.isse.gitecco.core.tree.nodes;

import at.jku.isse.gitecco.core.tree.visitor.TreeVisitor;
import at.jku.isse.gitecco.core.tree.visitor.Visitable;

/**
 * Class for representing a #include&lt;filename&gt; preprocessor statement
 */
public final class IncludeNode implements Visitable {
    private final String fileName;
    private final int lineInfo;

    public IncludeNode(String fileName, int lineInfo) {
        this.fileName = fileName;
        this.lineInfo = lineInfo;
    }

    /**
     * Retrieves the line info of this include node.
     * @return
     */
    public int getLineInfo() {
        return lineInfo;
    }

    /**
     * Retrieves the name of the file which is to be included into the source file.
     * @return
     */
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public void accept(TreeVisitor v) {
        v.visit(this);
    }
}
