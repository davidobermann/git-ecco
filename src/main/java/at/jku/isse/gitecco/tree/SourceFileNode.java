package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public class SourceFileNode extends FileNode{
    private final List<ConditionNode> children;

    public SourceFileNode(Node parent, String name, String path) {
        super(parent, name, path);
        children = new ArrayList<ConditionNode>();
    }
}
