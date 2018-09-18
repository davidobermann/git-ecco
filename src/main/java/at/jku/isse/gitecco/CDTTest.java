package at.jku.isse.gitecco;

import at.jku.isse.gitecco.cdt.PPStatement;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CDTTest {
    public static void main(String args[]) throws Exception {

        //Get file:
        final String code = readFile("C:\\obermanndavid\\git-to-ecco\\test_repo\\test.cpp", StandardCharsets.UTF_8);
        //parse file and prepare the translation unit
        final IASTTranslationUnit translationUnit = parse(code.toCharArray());
        //get all the preprocessor statements
        final IASTPreprocessorStatement[] ppstatements = translationUnit.getAllPreprocessorStatements();

        //translationUnit.accept(new MyASTVisitor(x -> System.out.print('-')));



        for (IASTPreprocessorStatement pps : ppstatements) {

            System.out.println(pps.toString());
            try {
                PPStatement pp = new PPStatement(pps);
                System.out.println("   --from: " + pp.getLineStart() + " ,to: "
                                + pp.getLineEnd());

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("------------");
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    public static IASTTranslationUnit parse(char[] code) throws Exception {

        FileContent fc = FileContent.create("/Path/ToResolveIncludePaths.cpp", code);
        Map<String, String> macroDefinitions = new HashMap<String, String>();
        String[] includeSearchPaths = new String[0];
        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex idx = null;
        int options = ILanguage.OPTION_PARSE_INACTIVE_CODE;
        IParserLogService log = new DefaultLogService();

        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
    }
}

