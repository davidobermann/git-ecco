package at.jku.isse.gitecco.core.tree.nodes;

import at.jku.isse.gitecco.core.tree.visitor.Visitable;

import java.util.Objects;

/**
 * SuperClass for all define nodes (define, undef)
 */
public abstract class DefineNode extends ConditionNode implements Comparable<DefineNode>, Visitable {
    private final String macroName;
    private final int lineInfo;

    public DefineNode(String name, int lineInfo) {
        this.lineInfo = lineInfo;
        this.macroName = name;
    }

    public String getMacroName() {
        return macroName;
    }

    public int getLineInfo() {
        return this.lineInfo;
    }

    /**
     * Determines if the given node is identical to this one.
     * ATTENTION: This does also compare the line info of the given node.
     *            use equals to check only for macro name.
     * @param n
     * @return
     */
    public boolean isIdentical(DefineNode n) {
        return this.equals(n) && this.lineInfo == n.lineInfo;
    }

    @Override
    public int compareTo(DefineNode o) {
        return getMacroName().compareTo(o.getMacroName());
    }

    /**
     * Determines if the defineNode is equal to this.
     * ATTENTION: equality means same macro name!
     *            use isIdentical() to check also for lineinfo.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefineNode that = (DefineNode) o;
        return lineInfo == that.lineInfo &&
                Objects.equals(macroName, that.macroName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macroName, lineInfo);
    }

    /**
     * Never needed for define nodes. Will always be accessed through parent.
     * @return null
     */
    @Override
    public Node getParent() {
        return null;
    }
}
