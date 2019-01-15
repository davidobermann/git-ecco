package at.jku.isse.gitecco.git;

import at.jku.isse.gitecco.cdt.Feature;
import at.jku.isse.gitecco.tree.nodes.ConditionalNode;

/**
 * Class to store a change.
 * A change is defined by two coordinates.<br>
 * <code>from</code> and <code>to</code>. <br>
 * These two int values do indicate the line number over which this change appears.
 */
public class Change {
    private final int from;
    private final int to;

    /**
     * Creates a new change.
     *
     * @param from int value of the starting line number of the change.
     * @param cnt  int value which indicates how many lines are covered by this change.
     */
    public Change(int from, int cnt) {
        this.from = from;
        this.to = from+cnt;
    }

    /**
     * Checks if a given Feature lays inside of this change.
     * Used to determine if a new Feature was added.
     *
     * @param feature The feature, which should be checked.
     * @return True if the Feature is in fact in this change, otherwise false.
     */
    public boolean contains(Feature feature) {
        return from<=feature.getStartingLineNumber() && to >= feature.getEndingLineNumber();
    }

    /**
     * Checks if a given ConditionalNode n lays inside of this change..
     * Used to determine if a new Feature was added.
     *
     * @param n The ConditionalNode to be checked.
     * @return True if the Feature is in fact in this change, otherwise false.
     */
    public boolean contains(ConditionalNode n) {
        return from <= n.getLineFrom() && to >= n.getLineTo();
    }

    /**
     * Gets the start of the change.
     *
     * @return int value of the starting line number of the change.
     */
    public int getFrom() {
        return this.from;
    }

    /**
     * Gets the end of the change.
     *
     * @return int value of the ending line number of the change.
     */
    public int getTo() {
        return this.to;
    }

    @Override
    public String toString() {
        return ""+from+","+to;
    }

}
