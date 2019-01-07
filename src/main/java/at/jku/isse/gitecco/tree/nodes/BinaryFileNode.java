package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.TreeVisitor;

/**
 * Class for representing a BinaryFile.
 * A Binary File does not contain any Features. It will be a leaf in the commit tree.
 */
public final class BinaryFileNode extends FileNode {

    public BinaryFileNode(Node parent, String filePath) {
        super(parent, filePath);
    }

    @Override
    public void accept(TreeVisitor v) {
        v.visit(this);
    }
}
