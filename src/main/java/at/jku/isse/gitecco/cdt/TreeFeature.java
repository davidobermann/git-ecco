package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.git.Change;

import java.util.ArrayList;
import java.util.List;


public class TreeFeature extends Feature{
    private static TreeFeature root;
    private final TreeFeature parent;
    private final List<TreeFeature> children;

    public TreeFeature(Feature self, TreeFeature parent) {
        super(self.getName(), self.getStartingLineNumber(), self.getEndingLineNumber());
        this.parent = parent;
        this.children = new ArrayList<TreeFeature>();
    }

    public TreeFeature(Feature self) {
        super(self.getName(), self.getStartingLineNumber(), self.getEndingLineNumber());
        this.parent = null;
        this.children = new ArrayList<TreeFeature>();
        root = this;
    }

    public TreeFeature getRoot() {
        return root;
    }

    public TreeFeature addChild(Feature child) {
        TreeFeature t = new TreeFeature(child, this);
        children.add(t);
        return t;
    }

    public TreeFeature getParent() {
        return this.parent;
    }

}

