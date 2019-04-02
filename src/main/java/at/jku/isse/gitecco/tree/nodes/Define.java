package at.jku.isse.gitecco.tree.nodes;

/**
 * Class to represent a #define preprocessor statement
 */
public final class Define extends DefineNodes{
    private final String macroExpansion;

    public Define(String name, String exp, int lineInfo) {
        super(name, lineInfo);
        this.macroExpansion = exp;
    }

    public String getMacroExpansion() {
        return macroExpansion;
    }

    @Override
    public String toString() {
        return "#define " + this.getMacroName() + " " + this.getMacroExpansion();
    }
}
