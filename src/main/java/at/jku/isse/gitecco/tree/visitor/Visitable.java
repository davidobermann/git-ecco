package at.jku.isse.gitecco.tree.visitor;

public interface Visitable {
    void accept(TreeVisitor v);
}
