package dev.crmodders.puzzle.extention;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

/**
 * The public api exposed to build scripts
 */
public interface PuzzleGradleExtension {
    /** Name of the extension */
    public static String NAME = "puzzle_loader";


    /**
     * Sets the path to the projects Access Widener
     * @since 1.0.0
     */
    RegularFileProperty getAccessWidenerPath();

    /**
     * Sets the directory of the game <br>
     * Defaults to the `run` directory at the root of the project
     * @since 1.0.0
     */
    RegularFileProperty getGameDir();

    /**
     * Weather to use Quilt's development environment <br>
     * Defaults to `true`, and should usually never be touched
     * @see <a href="https://github.com/QuiltMC/quilt-loader/wiki/System-Properties#loaderdevelopment">Quilts docs on the development argument</a>
     * @since 1.0.0
     */
    Property<Boolean> getUseDevelopmentRunEnvironment();




    /**
     * Gets the Maven/Ivy formatted string to Cosmic Reach with the specified ${version}
     * @param ver Version of Cosmic Reach
     * @return Gradle dependency ready formatted string
     * @since 1.0.0
     */
    public static String getCosmicReach(String ver) {
        return "finalforeach:cosmicreach:" + ver;
    }

    /**
     * Gets the Maven formatted string to Cosmic Quilt with the specified ${version}
     * @param ver Version of Cosmic Quilt
     * @return Gradle dependency ready formatted string
     * @since 1.0.0
     */
    public static String getPuzzleLoader(String ver) {
        return "dev.crmodders:puzzle-loader:" + ver;
    }
}
