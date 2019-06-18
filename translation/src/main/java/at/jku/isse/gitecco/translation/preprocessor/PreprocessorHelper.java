package at.jku.isse.gitecco.translation.preprocessor;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.OnlyExpandMacrosInIfsController;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.PreprocessorAPI;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class PreprocessorHelper {

    /**
     * Prepares a given directory (repository) for further processing by moving it to a new given location
     * and deletes the .git folder if it is given.
     * @param destDir the destination where the wroking copy should be moved to
     * @param srcDir the src location --> git repository
     * @param gitDir the location of the git folder which will be deleted after the copy process
     */
    private void prepareDirectory(File destDir, File srcDir, File gitDir) {
        if(destDir == null || srcDir == null) throw new IllegalArgumentException("src and dest cannot be null!");
        try {
            FileUtils.deleteDirectory(destDir);
            FileUtils.copyDirectory(srcDir, destDir);
            if(gitDir == null) return;
            FileUtils.deleteDirectory(gitDir);
        } catch (IOException e) {
            System.err.println("Error preparing repository for further processing!");
            e.printStackTrace();
        }
    }

    /**
     * Generates a "clean version" of the given repo/folder.
     * This means: all defines, includes and other PP-Statements are kept in the code.
     * BUT all macros used in #if,#ifdef,#ifndef statements in defines are expanded.
     * @param inPath
     * @param outPath
     */
    public void generateCleanVersion(String inPath, String outPath) {
        File srcDir = new File(inPath);
        File destDir = new File(outPath + "\\clean");

        prepareDirectory(destDir, srcDir, null);

        //Top part probably not needed
        PreprocessorAPI pp = new PreprocessorAPI(new OnlyExpandMacrosInIfsController());

        pp.setInlineIncludes(false);
        pp.setKeepIncludes(true);
        pp.setKeepDefines(true);

        File src = new File(inPath);
        File target = new File(outPath);

        pp.preprocess(src, target);
    }

    /**
     * Generates a variant of a given repository (inPath) in the destination outPath/data
     * @param configuration the configuration for preprocessing
     * @param inPath the path of the input folder
     * @param outPath the path of the output folder
     */
    public void generateVariants(Set<Feature> configuration, String inPath, String outPath) {
        File srcDir = new File(inPath);
        File destDir = new File(outPath + "\\data");
        File gitDir = new File(outPath + "\\data\\.git");

        prepareDirectory(destDir, srcDir, gitDir);

        //Top part probably not needed
        PreprocessorAPI pp = new PreprocessorAPI();

        pp.setInlineIncludes(false);
        pp.setKeepIncludes(true);
        pp.setKeepDefines(true);

        File src = new File(inPath);
        File target = new File(outPath);

        pp.preprocess(src, target);

    }

}
