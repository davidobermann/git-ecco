package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.conditionparser.ParsedCondition;
import at.jku.isse.gitecco.git.Change;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store a Feature. <br>
 * A Feature is defined by its name and its line information. <br>
 * <br> A feature usually looks like this:<br>
 * #if etest<br>
 * &emsp;printf("etest");<br>
 * #endif<br><br>
 * It may or may not contain changes. <br>
 */
public class Feature implements Comparable<Feature> {
    private final FeatureType featureType;
    private final int startingLineNumber;
    private final List<Change> changes;
    private final ParsedCondition[] conditions;
    private int endingLineNumber;

    /**
     * Creates a new Feature with the given parameters
     *
     * @param startingLineNumber
     * @param ft
     * @param conds
     */
    public Feature(int startingLineNumber, FeatureType ft, ParsedCondition... conds) {
        this.conditions = conds;
        this.startingLineNumber = startingLineNumber;
        this.featureType = ft;
        changes = new ArrayList<Change>();
    }

    /**
     * Creates a new Feature with the given parameters
     *
     * @param startingLineNumber
     * @param endingLineNumber
     * @param ft
     * @param conds
     */
    public Feature(int startingLineNumber, int endingLineNumber, FeatureType ft, ParsedCondition... conds) {
        this.conditions = conds;
        this.startingLineNumber = startingLineNumber;
        this.endingLineNumber = endingLineNumber;
        this.featureType = ft;
        changes = new ArrayList<Change>();
    }

    public ParsedCondition[] getConditions() {
        return conditions;
    }

    /**
     * gets the name of the feature
     *
     * @return the feature name
     */
    public String getNames() {
        String ret = "";
        if (conditions.length == 1) return conditions[0].getName();
        for (int i = 0; i<conditions.length-1; i++) {
            ret += conditions[i].getName()+", ";
        }
        if (conditions.length == 0) {
            //later on take care of !A and ifdef-->1 and ifndef-->0.
            ret += "1";
        } else {
            ret += conditions[conditions.length-1].getName();
        }
        return ret;
    }

    /**
     * Gets the Type of the Feature
     *
     * @return the Type of the Feature
     */
    public FeatureType getFeatureType() {
        return featureType;
    }

    /**
     * Checks if the feature contains a certain change.
     *
     * @param change
     * @return <code>true</code> if it contains the feature, otherwise <code>false</code>.
     */
    private boolean contains(Change change) {
        return (this.startingLineNumber<=change.getFrom() && this.endingLineNumber >= change.getTo());
    }

    /**
     * Checks if this Feature is BASE
     *
     * @return true if this feature is BASE
     */
    public boolean isBase() {
        return this.startingLineNumber == 0;
    }

    /**
     * Gets the line info where the feature starts.
     *
     * @return The line number of the start.
     */
    public int getStartingLineNumber() {
        return startingLineNumber;
    }

    /**
     * Gets the line info where the feature ends.
     *
     * @return The line number of the end.
     */
    public int getEndingLineNumber() {
        return endingLineNumber;
    }

    public void setEndingLineNumber(int lnr) {
        this.endingLineNumber = lnr;
    }

    /**
     * Checks if this feature contains a certain change
     * and adds this change to the feature if it does contain the change. <br>
     * Returns <code>true</code> if it was added.
     *
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
     * Adds a Change, but does not check if the Feature
     * Actually contains the Change.
     *
     * @param c The change to be added.
     */
    public void addUnchecked(Change c) {
        if (!changes.contains(c)) changes.add(c);
    }

    /**
     * Returns all the changes currently contained in this feature. <br>
     * Does not assure that already all changes have been checked.
     *
     * @return Array of the changes in this feature.
     */
    public Change[] getChanges() {
        return changes.toArray(new Change[changes.size()]);
    }

    /**
     * Checks if this feature has any changes.
     *
     * @return <code>true</codeY> if it does have changes. <code>fasle</code> if not.
     */
    public boolean hasChanges() {
        return changes.size()>0;
    }

    @Override
    public String toString() {
        return this.getNames()+": "+this.getStartingLineNumber()+" - "+this.getEndingLineNumber()+"  --> Typ: "+this.getFeatureType();
    }

    @Override
    public int compareTo(Feature o) {
        return Integer.compare(o.getStartingLineNumber(), this.getStartingLineNumber());
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Feature) && (this.compareTo((Feature) obj) == 0);
    }
}
