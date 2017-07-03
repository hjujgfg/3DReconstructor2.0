package org.hjujgfg.io;

import org.apache.log4j.Logger;
import org.hjujgfg.exceptions.DirectoryCreationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hjujgfg.consts.ImageConsts.*;

/**
 * Created by egor.lapidus on 30/06/2017.
 */
public class DirectoryHelper {

    private final static Logger logger = Logger.getLogger(DirectoryHelper.class);


    public void createDefaultDirs() throws DirectoryCreationException {
        File dir = new File(INPUT_DIR);
        if (!dir.exists()) {
            createDirsInner(INPUT_DIR);
            createDirsInner(RAW_INPUT);
            createDirsInner(PREPROCESSED_INPUT);
        }
        dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            createDirsInner(OUTPUT_DIR);
            createDirsInner(CONVOLVE_OUT);
        }
    }

    public void clearTemporalImages() throws DirectoryCreationException {
        try {
            Files.walk(Paths.get(INPUT_DIR, "preprocessed"), 1).forEach(filepath -> {
                if (filepath.toString().endsWith(".jpg") || filepath.toString().endsWith(".png")) {
                    logger.info("Removing: " + filepath.toString());
                    filepath.toFile().delete();
                }
            });
        } catch (IOException e) {
            throw new DirectoryCreationException("Error deleting old images", e);
        }
    }

    private void createDirsInner(String name) throws DirectoryCreationException {
        File f = new File(name);
        boolean success = true;
        if (!f.exists()) {
            success = f.mkdirs();
        }
        if (!success) {
            throw new DirectoryCreationException("Unable to create directory structure for " + name);
        }
    }
}
