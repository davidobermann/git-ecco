package at.jku.isse.gitecco.tree;


public abstract class Node {
    boolean changed;
    final Node parent;

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

    //TODO: implement some mechanism to add chidren.
    /* maybe better solution
    public abstract void addChild(Node n);
     */

}
