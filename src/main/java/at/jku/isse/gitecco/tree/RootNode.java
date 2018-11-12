package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public class RootNode extends Node{
    private final List<Node> children;

    public RootNode(Node parent) {
        super(parent);
        this.children = new ArrayList<Node>();
    }

}
