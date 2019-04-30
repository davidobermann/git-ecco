package at.jku.isse.gitecco.translation;

import org.anarres.cpp.*;

import java.io.File;
import java.io.IOException;

public class App {

    public static void main(String... args) {
        Preprocessor pp = new Preprocessor();
        pp.addFeature(Feature.DIGRAPHS);
        pp.addFeature(Feature.TRIGRAPHS);
        //pp.addFeature(Feature.LINEMARKERS);
        //pp.addWarning(Warning.IMPORT);
        pp.setListener(new DefaultPreprocessorListener());

        /*pp.getSystemIncludePath().add("/usr/local/include");
        pp.getSystemIncludePath().add("/usr/include");
        pp.getFrameworksPath().add("/System/Library/Frameworks");
        pp.getFrameworksPath().add("/Library/Frameworks");
        pp.getFrameworksPath().add("/Local/Library/Frameworks");*/

        try {
            pp.addInput(new File("ppfiles/test.cpp"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (;;) {
                Token token = pp.token();

                if (token == null || token.getType() == Token.EOF)
                    break;
                if (token.getType() == Token.CCOMMENT ||
                        token.getType() == Token.CPPCOMMENT ||
                        token.getType() == Token.WHITESPACE ||
                        token.getType() == Token.NL) {

                } else {
                    System.out.print(token.getText());
                }
                // http://shevek.github.io/jcpp/docs/javadoc/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
