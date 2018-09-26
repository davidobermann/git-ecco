package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.git.Change;

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

    public void linkChanges(Change[] changes) {
        for (Change c : changes) {
            linkChange(this,c);
        }
    }

    private boolean linkChange(TreeFeature tf, Change c) {
        for(TreeFeature f : tf.getChildren()){
            if(linkChange(f, c)) return true;
        }
        if(tf.checkAndAddChange(c)){
            System.out.println("Linked " + c.toString() + " to " + tf.getNames());
            return true;
        }
        return false;
    }

    public void printAll() {
        if(this.getParent() != null)
            System.out.println("Warning this is not a tree root!");
        printPreOrder(this, 0);
    }

    public List<TreeFeature> getChangedAsList() {
        List<TreeFeature> ret = new ArrayList<>();
        if(this.hasChanges()) ret.add(this);
        for(TreeFeature f : this.getChildren()) {
            ret.addAll(f.getChangedAsList());
        }
        return ret;
    }

    public List<TreeFeature> getToDeleteAsList() {
        List<TreeFeature> ret = new ArrayList<>();
        for(TreeFeature tf : this.getLeafs()){
            TreeFeature t = tf;
            while(t.parent != null && !t.hasChanges()) {
                if(!ret.contains(t))ret.add(t);
                t = t.parent;
            }
        }
        return ret;
    }

    private List<TreeFeature> getLeafs() {
        List<TreeFeature> ret = new ArrayList<>();
        if(this.isLeaf()) ret.add(this);
        for(TreeFeature tf : this.getChildren()) {
            ret.addAll(tf.getLeafs());
        }
        return ret;
    }

    private void printPreOrder(TreeFeature tf, int lvl){
        for (int i = 0; i < lvl; i++) {
            System.out.print("-");
        }
        System.out.print(tf.toString() + "\n");
        for(TreeFeature f : tf.getChildren()){
           printPreOrder(f, lvl+1);
        }
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

