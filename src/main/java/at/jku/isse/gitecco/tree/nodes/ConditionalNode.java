package at.jku.isse.gitecco.tree.nodes;

import at.jku.isse.gitecco.git.Change;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ConditionalNode extends ConditionNode {
    private int lineFrom = -1;
    private int lineTo = -1;
    private final List<ConditionBlockNode> children;

    public ConditionalNode(Node parent) {
        super(parent);
        children = new ArrayList<ConditionBlockNode>();
    }

    public boolean containsChange(Change c) {
        if(lineFrom == -1 || lineTo == -1) throw new IllegalStateException("line values have not been set correctly");
        return lineFrom <= c.getFrom() && lineTo >= c.getTo();
    }

    public ConditionBlockNode addChild(ConditionBlockNode n) {
        this.children.add(n);
        return n;
    }

    public void setLineFrom(int lineFrom) throws IllegalAccessException {
        if(this.lineFrom == -1) this.lineFrom = lineFrom;
        else throw new IllegalAccessException("Cannot set the line more than once");
    }

    public void setLineTo(int lineTo) throws IllegalAccessException {
        if(this.lineTo == -1) this.lineTo = lineTo;
        else throw new IllegalAccessException("Cannot set the line more than once");
    }

    public int getLineFrom() {
        return lineFrom;
    }

    public int getLineTo() {
        return lineTo;
    }

    public List<ConditionBlockNode> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
