package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.git.Change;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class to store a Feature. <br>
 * A Feature is defined by its name and its line information. <br>
 * <br> A feature usually looks like this:<br>
 * #if etest<br>
 * &emsp;printf("etest");<br>
 * #endif<br><br>
 * It may or may not contain changes. <br>
 * Same goes for the define value, which may or may not exist. <br>
 */
public class Feature implements Comparable<Feature> {
    private final String name;
    private final int startingLineNumber;
    private final int endingLineNumber;
    private final List<Change> changes;
    private final Optional<Double> define;

    /**
     * Creates a new Feature
     * @param name the name of the feature
     * @param define the define value if it exists or null if not
     * @param startingLineNumber line info where the feature begins
     * @param endingLineNumber line info where the feature ends
     */
    public Feature(String name, Optional<Double> define, int startingLineNumber, int endingLineNumber) {
        this.define = define;
        this.name = name;
        this.startingLineNumber = startingLineNumber;
        this.endingLineNumber = endingLineNumber;
        changes = new ArrayList<Change>();
    }

    /**
     * gets the name of the feature
     * @return the feature name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the feature contains a certain change.
     * @param change
     * @return <code>true</code> if it contains the feature, otherwise <code>false</code>.
     */
    private boolean contains(Change change) {
        return (this.startingLineNumber <= change.getFrom() && this.endingLineNumber >= change.getTo());
    }

    /**
     * Gets the line info where the feature starts.
     * @return The line number of the start.
     */
    public int getStartingLineNumber() {
        return startingLineNumber;
    }

    /**
     * Gets the line info where the feature ends.
     * @return The line number of the end.
     */
    public int getEndingLineNumber() {
        return endingLineNumber;
    }

    /**
     * Gets the define value as an <code>Optional&lt;Double&gt;</code>
     * @return The define value as an <code>Optional&lt;Double&gt;</code>
     */
    public Optional<Double> getDefine() {
        return define;
    }

    /**
     * Checks if this feature contains a certain change
     * and adds this change to the feature if it does contain the change. <br>
     * Returns <code>true</code> if it was added.
     * @param c The change which should
     * @return <code>true</code> if added, <code>false</code> if not.
     */
    public boolean checkAndAddChange(Change c) {
        if (this.contains(c)) {
            changes.add(c);
            return true;
        }
        return false;
    }

    /**
     * Returns all the changes currently contained in this feature. <br>
     * Does not assure that already all changes have been checked.
     * @return Array of the changes in this feature.
     */
    public Change[] getChanges() {
        return changes.toArray(new Change[changes.size()]);
    }

    /**
     * Checks if this feature has any changes.
     * @return <code>true</codeY> if it does have changes. <code>fasle</code> if not.
     */
    public boolean hasChanges() {
        return changes.size() > 0;
    }

    @Override
    public String toString() {
        return this.getName()+": "+this.getStartingLineNumber()+" - "+this.getEndingLineNumber();
    }

    @Override
    public int compareTo(Feature o) {
        return Integer.compare(o.getStartingLineNumber(), this.getStartingLineNumber());
    }
}
