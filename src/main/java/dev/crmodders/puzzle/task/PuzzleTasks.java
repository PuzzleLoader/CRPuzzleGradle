package dev.crmodders.puzzle.task;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import dev.crmodders.puzzle.CosmicPuzzleInfo;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import dev.crmodders.puzzle.task.tasks.RunClientTask;
import dev.crmodders.puzzle.task.tasks.RunUnmoddedClientTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.initialization.dsl.ScriptHandler;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;

import javax.inject.Inject;
import java.util.Collections;

public abstract class PuzzleTasks implements Runnable {
    @Inject
    protected abstract Project getProject();
    @Inject
    protected abstract TaskContainer getTasks();

    @Override
    public void run() {

        // Client Running
        getTasks().register("runClient", RunClientTask.class, t -> {
            t.setDescription("Runs the Cosmic Reach client w/ mods");
        });

        getTasks().register("runUnmoddedClient", RunUnmoddedClientTask.class, t -> {
            t.setDescription("Runs the Cosmic Reach client without mods");
        });

        // Jar Creation
        getTasks().register("buildSlimJar", Jar.class, t -> {
            t.setGroup(CosmicPuzzleInfo.GROUP);
            t.from(getProject().task("processResources").getOutputs().getFiles());
            t.from(getProject().getExtensions().getByType(SourceSetContainer.class).getByName("main").getAllJava().getClassesDirectory());

            t.setDescription("Builds a jar with no bundled dependencies");
        });

        getTasks().register("buildBundleJar", ShadowJar.class, t -> {
            t.setGroup(CosmicPuzzleInfo.GROUP);
            t.setConfigurations(Collections.singletonList(getProject().getConfigurations().getByName("bundle")));
            t.from(getProject().task("processResources").getOutputs().getFiles());
            t.from(getProject().getExtensions().getByType(SourceSetContainer.class).getByName("main").getAllJava().getClassesDirectory());

            t.setDescription("Builds a jar with all of the dependencies bundled");
        });

        getTasks().register("buildSourcesJar", Jar.class, t -> {
            t.setGroup(CosmicPuzzleInfo.GROUP);
            t.from(getProject().getExtensions().getByType(SourceSetContainer.class).getByName("main").getAllJava().getSourceDirectories());

            t.setDescription("Builds a jar with no bundled dependencies");
        });

        getTasks().register("buildAllJars", t -> {
            t.dependsOn("buildSlimJar");
            t.dependsOn("buildBundleJar");
            t.dependsOn("buildSourcesJar");
        });

    }

}
