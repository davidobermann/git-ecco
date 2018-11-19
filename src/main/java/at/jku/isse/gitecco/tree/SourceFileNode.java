package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public final class SourceFileNode extends FileNode {
    private final List<ConditionBlockNode> children;

    public SourceFileNode(Node parent, String filePath) {
        super(parent, filePath);
        children = new ArrayList<ConditionBlockNode>();
    }

    public void addChild(ConditionBlockNode n) {
        children.add(n);
    }
}
