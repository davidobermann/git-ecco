package at.jku.isse.gitecco.tree.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for storing a change + its affected features (parents).
 */
public class ComittableChange {
    private final String changed;
    private final Set<String> affected;

    public ComittableChange (String changed, Set<String> affected) {
        this.changed = changed;
        this.affected = new HashSet<>();
        this.affected.addAll(affected);
    }

    /**
     * Retrieves the actual changed condition.
     * @return
     */
    public String getChanged() {
        return changed;
    }

    /**
     * Retrieves the affected conditions/parents.
     * @return
     */
    public Set<String> getAffected() {
        return Collections.unmodifiableSet(affected);
    }
}
