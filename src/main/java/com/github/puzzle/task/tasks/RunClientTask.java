package com.github.puzzle.task.tasks;

import com.github.puzzle.Constants;
import com.github.puzzle.CosmicPuzzlePlugin;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.tasks.JavaExec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class RunClientTask extends JavaExec {
    public RunClientTask() {
        super();
        setGroup(Constants.Tasks.GROUP);

        dependsOn(getProject().getTasks().getByName("compileJava"));
        dependsOn(getProject().getTasks().getByName("processResources"));
        dependsOn(getProject().getTasks().getByName("classes"));

        Set<File> files = new HashSet<>();
        files.addAll(JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet().getCompileClasspath().getFiles());
        files.addAll(JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet().getRuntimeClasspath().getFiles());
        classpath(files);

        getMainClass().set("com.github.puzzle.loader.launch.Piece");
    }

    @Override
    public void exec() {
        setWorkingDir();

        setJvmArgs(getGameJvmArgs());

        super.exec();
    }

    public void setWorkingDir() {
        RegularFileProperty property = CosmicPuzzlePlugin.EXTENTION.getGameDir();
        if (property.isPresent())
            setWorkingDir(property.get());
        else
            setWorkingDir(getProject().file("run/"));
    }

    @Override
    public void setWorkingDir(File dir) {
        if (!dir.exists())
            dir.mkdirs();

        super.setWorkingDir(dir);
    }


    private List<String> getGameJvmArgs() {
        return new ArrayList<>();
    }
}
