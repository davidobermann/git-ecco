package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public class IFDEFConditionNode extends IFConditionNode{

    public IFDEFConditionNode(Node parent, int lineFrom, int lineTo, List<ELSEIFCondition> eic, ELSECondition ec, IFCondition ic) {
        super(parent, lineFrom, lineTo, eic, ec, ic);
    }
}
