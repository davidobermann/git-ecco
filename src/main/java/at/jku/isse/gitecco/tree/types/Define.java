package at.jku.isse.gitecco.tree.types;

import java.util.Objects;

public class Define implements Comparable<Define>{
    private final String macroName;
    private String macroExpansion;

    public Define(String name, String exp) {
        this.macroName = name;
        this.macroExpansion = exp;
    }

    public String getMacroName() {
        return macroName;
    }

    public String getMacroExpansion() {
        return macroExpansion;
    }

    public void setMacroExpansion(String macroExpansion) {
        this.macroExpansion = macroExpansion;
    }

    @Override
    public int compareTo(Define o) {
        return this.macroName.compareTo(o.macroName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Define define = (Define) o;
        return Objects.equals(macroName, define.macroName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macroName);
    }
}
