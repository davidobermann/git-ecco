package at.jku.isse.gitecco.conditionparser;


import java.util.Optional;

/**
 * Class for Features extracted from condition strings.
 */
public class ExtractedFeature {
    private final String name;
    private  final Optional<Double> define;

    /**
     * @param name The Feature name extracted from the Condition.
     * @param define The define value as an <code>Optional&lt;Double&gt;</code>
     */
    public ExtractedFeature(String name, Optional<Double> define){
        this.name = name;
        this.define = define;
    }

    /**
     * Gets the name of the extracted Feature
     * @return the name of the extracted Feature
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the define value as an optional.
     * @return The define value as an <code>Optional&lt;Double&gt;.
     * null</code> if just define is needed. Or the <code>Double</code> value for the definition.
     */
    public Optional<Double> getDefine() {
        return define;
    }
}
