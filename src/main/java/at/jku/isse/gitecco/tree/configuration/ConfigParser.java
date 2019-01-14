package at.jku.isse.gitecco.tree.configuration;

import at.jku.isse.gitecco.tree.nodes.*;

public class ConfigParser {

    public static String parseChanged(IFCondition node) {
        String ret = node.getCondition().replace('!','~').replace("&&","&").replace("||","|");
        return ret;
    }

    public static String parseChanged(IFNDEFCondition node) {
        String ret = node.getCondition().replace('!','~').replace("&&","&").replace("||","|");
        return ret;
    }

    public static String parseChanged(IFDEFCondition node) {
        String ret = node.getCondition().replace('!','~').replace("&&","&").replace("||","|");
        return ret;
    }

    public static String parseChanged(ELSECondition node) {
        String ret = "";
        return ret;
    }

}
