package at.jku.isse.gitecco.tree;

public abstract class Node {
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

    //TODO: implement a visitor pattern for tree traversal

}
