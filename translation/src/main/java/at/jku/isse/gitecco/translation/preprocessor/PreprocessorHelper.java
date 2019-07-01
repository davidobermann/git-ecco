package at.jku.isse.gitecco.translation.preprocessor;

import at.jku.isse.gitecco.core.type.Feature;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.OnlyExpandMacrosInIfsController;
import at.jku.isse.gitecco.translation.preprocessor.util.org.anarres.cpp.PreprocessorAPI;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class PreprocessorHelper {


    /**
     * Generates a "clean version" of the given repo/folder.
     * This means: all defines, includes and other PP-Statements are kept in the code.
     * BUT all macros used in #if,#ifdef,#ifndef statements in defines are expanded.
     * @param src
     * @param target
     */
    public void generateCleanVersion(File src, File target) {
        PreprocessorAPI pp = new PreprocessorAPI(new OnlyExpandMacrosInIfsController());
        pp.setInlineIncludes(false);
        pp.setKeepIncludes(true);
        pp.setKeepDefines(true);

        pp.preprocess(src, target);
    }

    /**
     * Generates a variant of a given repository (inPath) in the destination outPath/data
     * @param configuration the configuration for preprocessing
     * @param src input folder
     * @param target output folder
     */
    public void generateVariants(Map<Feature, Integer> configuration,File src, File target) {
        PreprocessorAPI pp = new PreprocessorAPI();

        pp.setInlineIncludes(false);
        pp.setKeepIncludes(true);
        pp.setKeepDefines(true);

        for (Map.Entry<Feature, Integer> entry : configuration.entrySet()) {
            //TODO: one of them migth be removable since we extract the solution form the clean version
            pp.addMacro(entry.getKey().getName(),entry.getValue().toString());
            pp.addMacro("SWITCH_ENABLED_"+entry.getKey().getName(),entry.getValue().toString());
        }

        pp.preprocess(src, target);

    }

}
