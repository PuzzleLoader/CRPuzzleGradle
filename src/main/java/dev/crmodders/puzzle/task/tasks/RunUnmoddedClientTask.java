package dev.crmodders.puzzle.task.tasks;

import dev.crmodders.puzzle.Constants;
import dev.crmodders.puzzle.CosmicPuzzlePlugin;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.plugins.internal.JavaPluginHelper;
import org.gradle.api.tasks.JavaExec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class RunUnmoddedClientTask extends JavaExec {
    public RunUnmoddedClientTask() {
        super();
        setGroup(Constants.Tasks.GROUP);

        classpath(JavaPluginHelper.getJavaComponent(getProject()).getMainFeature().getSourceSet().getRuntimeClasspath().getFiles());

        getMainClass().set("finalforeach.cosmicreach.lwjgl3.Lwjgl3Launcher");
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
