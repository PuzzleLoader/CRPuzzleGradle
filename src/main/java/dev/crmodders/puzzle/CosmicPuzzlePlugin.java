package dev.crmodders.puzzle;

import dev.crmodders.puzzle.accesswidener.AWPlugin;
import dev.crmodders.puzzle.configuration.PuzzleConfigurations;
import dev.crmodders.puzzle.configuration.PuzzleRepositoriesPlugin;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import dev.crmodders.puzzle.task.PuzzleTasks;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

import java.util.List;

public class CosmicPuzzlePlugin implements Plugin<Project> {
    /**
     * An ordered list of setup job classes.
     */
    private static final List<Class<? extends Runnable>> SETUP_JOBS = List.of(
            PuzzleRepositoriesPlugin.class,
            PuzzleConfigurations.class,
            AWPlugin.class,
            PuzzleTasks.class
    );
    /**
     * The extention containing the projects configs
     */
    public static PuzzleGradleExtension EXTENTION;


    @Override
    public void apply(Project project) {
        Logger log = project.getLogger();
        log.lifecycle("Starting "+ CosmicPuzzleInfo.NAME +" "+ CosmicPuzzleInfo.VERSION);

        EXTENTION = project.getExtensions().create(PuzzleGradleExtension.NAME, PuzzleGradleExtension.class);

        for (Class<? extends Runnable> jobClass : SETUP_JOBS) {
            project.getObjects().newInstance(jobClass).run();
        }
    }
}
