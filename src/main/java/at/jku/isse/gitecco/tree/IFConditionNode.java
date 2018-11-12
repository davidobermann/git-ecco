package at.jku.isse.gitecco.tree;

import java.util.List;

public class IFConditionNode extends ConditionNode{
    final IFCondition ifCondition;
    final List<ELSEIFCondition> elseifConditions;
    final ELSECondition elseCondition;

    public IFConditionNode(Node parent, int lineFrom, int lineTo, List<ELSEIFCondition> eic, ELSECondition ec, IFCondition ic) {
        super(parent, lineFrom, lineTo);
        elseifConditions = eic;
        elseCondition = ec;
        ifCondition = ic;
    }
}
