package at.jku.isse.gitecco.conditionparser;

public class ParsedCondition {
    private final double definition;
    private final String name;

    /**
     * Creates a new Parsed Condition
     * @param name
     * @param definition
     */
    public ParsedCondition(String name, double definition) {
        this.definition = definition;
        this.name = name;
    }

    /**
     * Gets the Name of the ConditionContent
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the definition of the ConditionContent.
     * 0 stands for either 0 or no definition.
     * Which at last will not matter becoause it is only needed for preprocessing.
     * @return
     */
    public double getDefinition() {
        return definition;
    }
}
