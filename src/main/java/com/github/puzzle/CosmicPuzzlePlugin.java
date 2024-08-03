package com.github.puzzle;

import com.github.puzzle.accessmanipulator.AMPlugin;
import com.github.puzzle.configuration.PuzzleConfigurations;
import com.github.puzzle.configuration.PuzzleRepositoriesPlugin;
import com.github.puzzle.extention.PuzzleGradleExtension;
import com.github.puzzle.task.PuzzleTasks;
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
            AMPlugin.class,
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
            System.out.println("Starting JobClass \"" + jobClass.getName() + "\"");
            project.getObjects().newInstance(jobClass).run();
            System.out.println("Finished Job");
        }
    }
}
