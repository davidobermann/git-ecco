package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.Visitable;

/**
 * Abstract Class for Nodes of a tree for a commit.
 * A commit tree consists of a RootNode. The Root Node has files, each file may have features.
 * Each Feature can be marked as changed.
 */
public abstract class Node implements Visitable {
    private final Node parent;
    private boolean changed;

    public Node(Node parent) {
        this.parent = parent;
        changed = false;
    }

    /**
     * Checks if the node is marked as changed
     * @return true if it is marked as changed, false otherwise.
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Marks the Node as changed.
     * This operation is only for marking it as changed.
     * Once it is changed, there is no way to undo this operation.
     */
    public void setChanged() {
        changed = true;
    }

    /**
     * Retrieves the parent of the node.
     * @return
     */
    public Node getParent() {
        return this.parent;
    }

}
