package at.jku.isse.gitecco.tree;

import java.util.ArrayList;
import java.util.List;

public class IFNDEFConditionNode extends IFConditionNode {

    public IFNDEFConditionNode(Node parent, int lineFrom, int lineTo, List<ELSEIFCondition> eic, ELSECondition ec, IFCondition ic) {
        super(parent, lineFrom, lineTo, eic, ec, ic);
    }
}
