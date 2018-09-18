package at.jku.isse.gitecco.git;

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
     * @param from int value of the starting line number of the change.
     * @param cnt int value which indicates how many lines are coverd by this change.
     */
    public Change(int from, int cnt){
        this.from = from;
        this.to = from + cnt - 1;
    }

    /**
     * Gets the start of the change.
     * @return int value of the starting line number of the change.
     */
    public int getFrom() {
        return this.from;
    }

    /**
     * Gets the end of the change.
     * @return int value of the ending line number of the change.
     */
    public int getTo() {
        return this.to;
    }

    @Override
    public String toString(){
        return "" + from + "," + to;
    }

}
