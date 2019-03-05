package at.jku.isse.gitecco.cdt;

import at.jku.isse.gitecco.tree.nodes.*;
import org.eclipse.cdt.core.dom.ast.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public SourceFileNode parseToTree(IASTPreprocessorStatement[] ppstatements, int linecnt, SourceFileNode srcfilenode) throws Exception {
        //create artificial BASE Node
        ConditionBlockNode baseNode = new ConditionBlockNode();
        baseNode.setIfBlock(new IFCondition(baseNode,"BASE"));
        baseNode.getIfBlock().setLineFrom(0);
        baseNode.getIfBlock().setLineTo(linecnt);
        //count for non accepted statements
        int nacnt = 0;
        //count for accepted statement
        int acnt = 0;
        //attach artificial base node to the file
        srcfilenode.setBase(baseNode);

        PPStatement pp;

        //var for the current block node to which conditionals are added
        ConditionBlockNode currentBlock = baseNode;
        //var for current Conditional node which gets filled with children etc.
        ConditionalNode currentConditional = baseNode.getIfBlock();

        for(IASTPreprocessorStatement pps : ppstatements) {
            /* starting statements*/
            if (pps instanceof IASTPreprocessorIfStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                if(condName.contains("<") || condName.contains(">") || condName.contains("==")){
                    nacnt++;
                    continue;
                }

                currentBlock = new ConditionBlockNode(currentConditional);
                currentConditional.addChild(currentBlock);
                currentConditional = currentBlock.setIfBlock(new IFCondition(currentBlock, condName));
                currentConditional.setLineFrom(pp.getLineStart());
                acnt++;

            } else if (pps instanceof IASTPreprocessorIfdefStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                if(condName.contains("<") || condName.contains(">") || condName.contains("==")){
                    nacnt++;
                    continue;
                }
                currentBlock = new ConditionBlockNode(currentConditional);
                currentConditional.addChild(currentBlock);
                currentConditional = currentBlock.setIfBlock(new IFDEFCondition(currentBlock, condName));
                currentConditional.setLineFrom(pp.getLineStart());
                acnt++;

            } else if (pps instanceof IASTPreprocessorIfndefStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                if(condName.contains("<") || condName.contains(">") || condName.contains("==")){
                    nacnt++;
                    continue;
                }
                currentBlock = new ConditionBlockNode(currentConditional);
                currentConditional.addChild(currentBlock);
                currentConditional = currentBlock.setIfBlock(new IFNDEFCondition(currentBlock, condName));
                currentConditional.setLineFrom(pp.getLineStart());
                acnt++;

            /* ending statements */
            } else if (pps instanceof IASTPreprocessorElifStatement) {
                pp = new PPStatement(pps);
                String condName = CDTHelper.getCondName(pp.getStatement());
                if(condName.contains("<") || condName.contains(">") || condName.contains("==")){
                    nacnt++;
                    continue;
                }
                currentConditional.setLineTo(pp.getLineEnd());
                currentBlock = currentConditional.getParent();
                currentConditional = currentBlock.addElseIfBlock(new IFCondition(currentBlock,condName));
                currentConditional.setLineFrom(pp.getLineStart());
                acnt++;

            } else if (pps instanceof IASTPreprocessorElseStatement) {
                if(nacnt > 0 && acnt == 0) continue;
                pp = new PPStatement(pps);
                currentConditional.setLineTo(pp.getLineEnd());
                currentBlock = currentConditional.getParent();
                currentConditional = currentBlock.setElseBlock(new ELSECondition(currentBlock));
                currentConditional.setLineFrom(pp.getLineStart());

            } else if (pps instanceof IASTPreprocessorEndifStatement) {
                if(nacnt > 0 && acnt == 0) {
                    nacnt--;
                    continue;
                }
                pp = new PPStatement(pps);
                currentConditional.setLineTo(pp.getLineEnd());
                currentBlock = currentConditional.getParent();
                currentConditional = currentBlock.getParent();
                acnt--;
            }
        }
        return srcfilenode;
    }

    //removes the defined() macro and replaces it by simply the variable
    //the variable will be defined therefore and coan will expand the macro --> should work.
    private String removeDefindeMacro(String s) {
        Pattern p = Pattern.compile("defined\\((.*?)\\)");
        Matcher m = p.matcher(s);
        while(m.find()) {
            s = s.replaceFirst("defined\\((.*?)\\)",m.group(1));
        }
        return s;
    }

}
