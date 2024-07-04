package dev.crmodders.puzzle.extention;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

/**
 * The public api exposed to build scripts
 */
public interface PuzzleGradleExtension {
    /** Name of the extension */
    static String NAME = "puzzle_loader";


    /**
     * Sets the path to the projects Access Widener
     * @since 1.0.0
     */
    RegularFileProperty getFabricAccessWidenerPath();

    /**
     * Sets the path to the projects Access Transformer
     * @since 1.0.25-dev
     */
    RegularFileProperty getForgeAccessTransformerPath();

    /**
     * Sets the path to the projects Access Widener
     * @since 1.0.25-dev
     */
    RegularFileProperty getAccessManipulatorPath();

    /**
     * Sets the directory of the game <br>
     * Defaults to the `run` directory at the root of the project
     * @since 1.0.0
     */
    RegularFileProperty getGameDir();

    /**
     * Gets the Maven/Ivy formatted string to Cosmic Reach with the specified ${version}
     * @param ver Version of Cosmic Reach
     * @return Gradle dependency ready formatted string
     * @since 1.0.0
     */
    static String getCosmicReach(String ver) {
        return "finalforeach:cosmicreach:" + ver;
    }

    /**
     * Gets the Maven formatted string to Puzzle Loader with the specified ${version}
     * @param ver Version of PuzzleLoader
     * @return Gradle dependency ready formatted string
     * @since 1.0.0
     */
    static String getPuzzleLoader(String ver) {
        return "dev.crmodders:puzzle-loader:" + ver;
    }

    /**
     * Gets the Maven formatted string to Access Manipulators with the specified ${version}
     * @param ver Version of Access Manipulators
     * @return Gradle dependency ready formatted string
     * @since 1.0.0
     */
    static String getAccessManipulators(String ver) {
        return "dev.crmodders:access-manipulators:" + ver;
    }
}
