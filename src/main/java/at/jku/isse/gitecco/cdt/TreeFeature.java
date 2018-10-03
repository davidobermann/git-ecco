package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.git.Change;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for storing features as a tree of features. <br>
 * Each Subtree can be represented by this TreeFeature class.
 */
public class TreeFeature extends Feature {
    private static TreeFeature root;
    private final TreeFeature parent;
    private final List<TreeFeature> children;

    /**
     * Creates a new Tree root.
     *
     * @param self The Feature to be stored.
     */
    public TreeFeature(Feature self) {
        super(self.getStartingLineNumber(), self.getEndingLineNumber(), self.getName());
        this.parent = null;
        this.children = new ArrayList<TreeFeature>();
        root = this;
    }

    private TreeFeature(Feature self, TreeFeature parent) {
        super(self.getStartingLineNumber(), self.getEndingLineNumber(), self.getName());
        this.parent = parent;
        this.children = new ArrayList<TreeFeature>();
    }

    /**
     * Gets the root of the Tree.
     *
     * @return The root of the tree.
     */
    public TreeFeature getRoot() {
        return root;
    }

    /**
     * Adds a Child to the tree.
     *
     * @param child The Feature which should be added as a child to the current node.
     * @return The added Feature/The newly created child.
     */
    public TreeFeature addChild(Feature child) {
        TreeFeature t = new TreeFeature(child, this);
        children.add(t);
        return t;
    }

    /**
     * Links the given Changes to the Tree. <br>
     * Preferably used on the tree root.
     *
     * @param changes Array of the changes which should be linked to the feature.
     */
    public void linkChanges(Change[] changes) {
        for (Change c : changes) {
            linkChange(this, c);
        }
    }

    private boolean linkChange(TreeFeature tf, Change c) {
        for (TreeFeature f : tf.getChildren()) {
            if (linkChange(f, c)) return true;
        }

        if(c.contains(tf)) {
            tf.addUnchecked(c);
            System.out.println("Linked "+c.toString()+" to "+tf.getNames());
        }

        if (tf.checkAndAddChange(c)) {
            System.out.println("Linked "+c.toString()+" to "+tf.getNames());
            return true;
        }
        return false;
    }

    /**
     * Prints the entire tree under the node which this method was called from.
     */
    public void printAll() {
        printPreOrder(this, 0);
    }

    /**
     * Gets all the changed Features of the Tree and returns them as a List oft TreeFeatures
     *
     * @return List of TreeFeatures containing all changed Features of the Tree.
     */
    public List<TreeFeature> getChangedAsList() {
        final List<TreeFeature> ret = new ArrayList<>();
        if (this.hasChanges()) ret.add(this);
        for (TreeFeature f : this.getChildren()) {
            ret.addAll(f.getChangedAsList());
        }
        return ret;
    }

    /**
     * Gets all the Features which can and should be cut out of the source file.
     *
     * @return All the Features of the sub trees which are fully unchanged.
     */
    public List<TreeFeature> getToDelete() {
        final List<TreeFeature> changed = new ArrayList<>();
        for (TreeFeature tf : getChangedAsList()) {
            TreeFeature t = tf;
            while (t != null) {
                if (!changed.contains(t)) {
                    changed.add(t);
                }
                t = t.parent;
            }
        }
        return filterTree(changed);
    }

    private List<TreeFeature> filterTree(List<TreeFeature> changedSubtrees) {
        final List<TreeFeature> ret = new ArrayList<>();
        if (!changedSubtrees.contains(this)) ret.add(this);
        for (TreeFeature tf : this.getChildren()) {
            ret.addAll(tf.filterTree(changedSubtrees));
        }
        return ret;
    }

    private void printPreOrder(TreeFeature tf, int lvl) {
        for (int i = 0; i < lvl; i++) {
            System.out.print("-");
        }
        System.out.print(tf.toString()+"\n");
        for (TreeFeature f : tf.getChildren()) {
            printPreOrder(f, lvl+1);
        }
    }

    /**
     * Gets the parent TreeFeature node.
     *
     * @return The parent TreeFeature node.
     */
    public TreeFeature getParent() {
        return this.parent;
    }

    /**
     * Gets all the children this TreeFeature has as a List of TreeFeatures.
     *
     * @return Children of this TreeFeature as a List.
     */
    public List<TreeFeature> getChildren() {
        return Collections.unmodifiableList(children);
    }

}

