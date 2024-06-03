package dev.crmodders.puzzle;

import org.gradle.internal.impldep.org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class CosmicPuzzleInfo {
    public static final String ID = "cr_puzzle_gradle";
    public static final String VERSION;
    public static final String GROUP;
    public static final String NAME;

    static {
        // Defaults if nothing is found
        String name = "Cosmic Reach Puzzle Gradle";
        String group = "dev.crmodders";
        String version = "0.0.0";

        try {
            if (CosmicPuzzleInfo.class.getPackage().getSpecificationVersion() != null) {
                // Ideally, these would be used, but sometimes they don't work
                name = CosmicPuzzleInfo.class.getPackage().getSpecificationTitle();
                group = CosmicPuzzleInfo.class.getPackage().getSpecificationVendor();
                version = CosmicPuzzleInfo.class.getPackage().getSpecificationVersion();
            } else {
                // And if those on top don't work, then this will be called
                Attributes attributes = new JarFile(
                        new File(CosmicPuzzleInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                ).getManifest().getMainAttributes();
                name = attributes.getValue("Specification-Title");
                group = attributes.getValue("Specification-Vendor");
                version = attributes.getValue("Specification-Version");
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(CosmicPuzzleInfo.class);
        }

        NAME = name;
        GROUP = group;
        VERSION = version;
    }
}
