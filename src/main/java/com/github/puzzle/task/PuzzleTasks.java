package com.github.puzzle.task;

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;
import com.github.puzzle.Constants;
import com.github.puzzle.accessmanipulator.AMPlugin;
import com.github.puzzle.task.tasks.RunClientTask;
import com.github.puzzle.task.tasks.RunUnmoddedClientTask;
import org.gradle.api.Project;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;

public abstract class PuzzleTasks implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AMPlugin.class);

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
            t.setGroup(Constants.Tasks.GROUP);
            t.dependsOn(getProject().getTasks().getByName("compileJava"));
            t.dependsOn(getProject().getTasks().getByName("processResources"));
            t.from(getProject().getTasks().getByName("processResources").getOutputs().getFiles());
            t.from(JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet().getOutput().getClassesDirs().getFiles());

            t.getArchiveVersion().set(t.getArchiveVersion().get()+"-slim");
            t.setDescription("Builds a jar with no bundled dependencies");
        });

        getTasks().register("buildBundleJar", ShadowJar.class, t -> {
            t.setGroup(Constants.Tasks.GROUP);
            t.dependsOn(getProject().getTasks().getByName("compileJava"));
            t.dependsOn(getProject().getTasks().getByName("processResources"));
            t.setConfigurations(Collections.singletonList(getProject().getConfigurations().getByName("bundle")));
            t.from(getProject().getTasks().getByName("processResources").getOutputs().getFiles());
            t.from(JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet().getOutput().getClassesDirs().getFiles());

            t.getArchiveVersion().set(t.getArchiveVersion().get()+"-bundle");
            t.setDescription("Builds a jar with all of the dependencies bundled");
        });

        getTasks().register("buildSourcesJar", Jar.class, t -> {
            t.setGroup(Constants.Tasks.GROUP);
            t.from(JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet().getAllJava());

            t.getArchiveVersion().set(t.getArchiveVersion().get()+"-sources");
            t.setDescription("Builds a jar with no bundled dependencies");
        });

        getTasks().register("buildAllJars", t -> {
            t.setGroup(Constants.Tasks.GROUP);
            t.dependsOn("buildSlimJar");
            t.dependsOn("buildBundleJar");
            t.dependsOn("buildSourcesJar");
        });

    }

}
