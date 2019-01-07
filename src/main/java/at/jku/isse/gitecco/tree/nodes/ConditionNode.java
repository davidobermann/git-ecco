package at.jku.isse.gitecco.tree.nodes;

/**
 * Abstract class for representing a condition.
 * A condition may be an IF, IFDEF, IFNDEF, ELSE Condition.
 */
public abstract class ConditionNode extends Node {

    public ConditionNode(Node parent) {
        super(parent);
    }

    /**
     * Retrieves the SourceFileNode which contains this node.
     * @return
     */
    public SourceFileNode getContainingSourceFile() {
        Node temp = this;
        while (!(temp instanceof SourceFileNode) && temp.getParent() != null) {
            temp = temp.getParent();
        }
        return (SourceFileNode) temp;
    }
}
