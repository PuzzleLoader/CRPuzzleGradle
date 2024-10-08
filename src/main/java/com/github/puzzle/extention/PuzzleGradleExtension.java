package com.github.puzzle.extention;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.gradle.api.file.RegularFileProperty;

/**
 * The public api exposed to build scripts
 */
public interface PuzzleGradleExtension {
    /** Name of the extension */
    String NAME = "puzzle_loader";
    ComparableVersion PUZZLE_VERSION_REFACTOR = new ComparableVersion("2.0.0");


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
     * Gets the Maven formatted string to Puzzle Paradox with the specified ${version}
     * @param ver Version of PuzzleParadox
     * @return Gradle dependency ready formatted string
     * @since 1.1.0
     */
    static String getPuzzleParadox(String ver) {
        return "com.github.PuzzleLoader:Paradox:" + ver;
    }

    /**
     * Gets the Maven formatted string to Puzzle Loader with the specified ${version}
     * @param ver Version of PuzzleLoader
     * @return Gradle dependency ready formatted string
     * @since 1.0.0
     */
    static String getPuzzleLoader(String ver) {
        return "com.github.PuzzleLoader:PuzzleLoader:" + ver;
    }

    /**
     * Gets the Maven formatted string to Access Manipulators with the specified ${version}
     * @param ver Version of Access Manipulators
     * @return Gradle dependency ready formatted string
     * @since 1.0.0
     */
    static String getAccessManipulators(String ver) {
        return "com.github.PuzzleLoader:access_manipulators:" + ver;
    }
}
