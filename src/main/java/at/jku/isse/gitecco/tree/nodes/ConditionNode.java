package at.jku.isse.gitecco.tree.nodes;

public abstract class ConditionNode extends Node {

    public ConditionNode(Node parent) {
        super(parent);
    }

    public SourceFileNode getContainingSourceFile() {
        Node temp = this;
        while (!(temp instanceof SourceFileNode) && temp.getParent() != null) {
            temp = temp.getParent();
        }
        return (SourceFileNode) temp;
    }
}
