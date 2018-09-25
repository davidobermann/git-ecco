package at.jku.isse.gitecco.cdt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TreeFeature extends Feature{
    private static TreeFeature root;
    private final TreeFeature parent;
    private final List<TreeFeature> children;

    public TreeFeature(Feature self, TreeFeature parent) {
        super(self.getStartingLineNumber(), self.getEndingLineNumber(), self.getName());
        this.parent = parent;
        this.children = new ArrayList<TreeFeature>();
    }

    public TreeFeature(Feature self) {
        super(self.getStartingLineNumber(), self.getEndingLineNumber(), self.getName());
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

    public  boolean isLeaf() {
        return children.size() == 0;
    }

    public List<TreeFeature> getChildren(){
        return Collections.unmodifiableList(children);
    }

}

