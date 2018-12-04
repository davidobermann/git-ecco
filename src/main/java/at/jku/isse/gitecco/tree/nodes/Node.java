package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.tree.visitor.Visitable;

public abstract class Node implements Visitable {
    private final Node parent;
    private boolean changed;

    public Node(Node parent) {
        this.parent = parent;
        changed = false;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged() {
        changed = true;
    }

    public Node getParent() {
        return this.parent;
    }

}
