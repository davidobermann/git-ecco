package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.conditionparser.ConditionParser;
import at.jku.isse.gitecco.conditionparser.ParsedCondition;
import at.jku.isse.gitecco.tree.*;
import org.eclipse.cdt.core.dom.ast.*;

/**
 * Class for parsing features in the c++ file.
 */
public class FeatureParser {

    /**
     * Retains all the features contained in the file as a tree of features --> TreeFeature
     *
     * @param ppstatements
     * @param linecnt
     * @return The root of the tree.
     * @throws Exception
     */
    public SourceFileNode parseToTreeDefNew(IASTPreprocessorStatement[] ppstatements, int linecnt, SourceFileNode srcfilenode) throws Exception {
        //create artificial BASE Node
        ConditionBlockNode baseNode = new ConditionBlockNode(srcfilenode);
        baseNode.setIfBlock(new IFCondition(baseNode,"BASE"));
        baseNode.getIfBlock().setLineFrom(0);
        baseNode.getIfBlock().setLineTo(linecnt);

        srcfilenode.addChild(baseNode);

        PPStatement pp;

        ConditionBlockNode currentBlock = baseNode;
        ConditionalNode currentConditional = baseNode.getIfBlock();

        for(IASTPreprocessorStatement pps : ppstatements) {
            pp = new PPStatement(pps);
            if (pps instanceof IASTPreprocessorIfStatement) {
                String condName = CDTHelper.getCondName(pp.getStatement());
                currentBlock = new ConditionBlockNode(currentConditional);
                currentBlock.setIfBlock(new IFCondition(currentBlock, condName)).setLineTo(pp.getLineStart());
                currentConditional.addChild(currentBlock);
            } else if (pps instanceof IASTPreprocessorIfdefStatement) {
                String condName = CDTHelper.getCondName(pp.getStatement());
                currentBlock = new ConditionBlockNode(currentConditional);
                currentBlock.setIfBlock(new IFNDEFCondition(currentBlock, condName)).setLineTo(pp.getLineStart());
                currentConditional.addChild(currentBlock);
            } else if (pps instanceof IASTPreprocessorIfndefStatement) {
                String condName = CDTHelper.getCondName(pp.getStatement());
                currentBlock = new ConditionBlockNode(currentConditional);
                currentBlock.setIfBlock(new IFCondition(currentBlock, condName)).setLineTo(pp.getLineStart());
                currentConditional.addChild(currentBlock);

                //TODO: Down from here: clean up the mess, find a strategy!
                //TODO: Validate the below code.

            } else if (pps instanceof IASTPreprocessorElifStatement) {
                String condName = CDTHelper.getCondName(pp.getStatement());
                currentConditional.setLineTo(pp.getLineEnd());
                currentBlock = (ConditionBlockNode) currentBlock.getParent().getParent();
                currentConditional = currentBlock.addElseIfBlock(new IFCondition(currentBlock,condName));
                currentConditional.setLineFrom(pp.getLineStart());
            } else if (pps instanceof IASTPreprocessorElseStatement) {
                currentConditional.setLineTo(pp.getLineEnd());
                currentBlock = (ConditionBlockNode) currentBlock.getParent().getParent();
                currentConditional = currentBlock.setElseBlock(new ELSECondition(currentBlock));
                currentConditional.setLineFrom(pp.getLineStart());
            } else if (pps instanceof IASTPreprocessorEndifStatement) {
                currentConditional.setLineTo(pp.getLineEnd());
                currentBlock = (ConditionBlockNode) currentBlock.getParent().getParent();
            }
        }
        return srcfilenode;
    }

    /**
     * Retains all the features contained in the file as a tree of features --> TreeFeature
     *
     * @param ppstatements
     * @param linecnt
     * @return The root of the tree.
     * @throws Exception
     */
    public TreeFeature parseToTreeDef(IASTPreprocessorStatement[] ppstatements, int linecnt) throws Exception {
        final TreeFeature root = new TreeFeature(new Feature(0, linecnt, FeatureType.IF, new ParsedCondition("BASE", 1)));
        TreeFeature currentNode = root;
        PPStatement pp;

        for (IASTPreprocessorStatement pps : ppstatements) {
            if (pps instanceof IASTPreprocessorIfStatement
                    || pps instanceof IASTPreprocessorIfdefStatement
                    || pps instanceof IASTPreprocessorIfndefStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                ParsedCondition[] pc = ConditionParser.parseConditionWithDefition(condName);
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.IF, pc));
            } else if (pps instanceof IASTPreprocessorElifStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                ParsedCondition[] pc = ConditionParser.parseConditionWithDefition(condName);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELIF, pc));
            } else if (pps instanceof IASTPreprocessorElseStatement) {
                pp = new PPStatement(pps);
                ParsedCondition[] pc = currentNode.getParent().getConditions();
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
                currentNode = currentNode.addChild(new Feature(pp.getLineStart(), FeatureType.ELSE, pc));
            } else if (pps instanceof IASTPreprocessorEndifStatement) {
                pp = new PPStatement(pps);
                currentNode.setEndingLineNumber(pp.getLineEnd());
                currentNode = currentNode.getParent();
            }
        }

        return root;
    }

}
