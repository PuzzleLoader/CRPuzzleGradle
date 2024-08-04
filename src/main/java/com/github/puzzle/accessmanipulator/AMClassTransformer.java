package com.github.puzzle.accessmanipulator;

import com.github.puzzle.access_manipulators.AccessManipulators;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class AMClassTransformer implements TransformAction<AMClassTransformer.AMClassParameters> {

    public interface AMClassParameters extends TransformParameters {
        @Input
        ListProperty<File> getAccessManipulatorPaths();
    }

    @InputArtifact
    public abstract Provider<FileSystemLocation> getInputArtifact();

    public static void clearManipulatorCaches() {
        AccessManipulators.affectedClasses.clear();
        AccessManipulators.classesToModify.clear();
        AccessManipulators.methodsToModify.clear();
        AccessManipulators.fieldsToModify.clear();
    }

    public static boolean isInConfiguration(Configuration configuration, File file) {
        return configuration.getFiles().contains(file);
    }

    public static File transformJar(Project project, File file) {
        if (!file.getName().endsWith(".jar")) return file;
        try {
            if (!AMPlugin.hasManipulators) return file;

            ZipInputStream inputJar = new ZipInputStream(new FileInputStream(file));

            File puzzleCache = new File(project.getProjectDir().getAbsolutePath() + "/.gradle/puzzle-cache/");
            if (!puzzleCache.exists()) puzzleCache.mkdirs();
            File transformedFile = new File(project.getProjectDir().getAbsolutePath() + "/.gradle/puzzle-cache/" + file.getName().replaceAll("\\.jar", ".transformed.jar"));
            if (transformedFile.exists()) transformedFile.delete();
            ZipOutputStream outputJar = new ZipOutputStream(new FileOutputStream(
                    transformedFile
            ));

            ZipEntry currentEntry = inputJar.getNextEntry();
            while (currentEntry != null) {
                outputJar.putNextEntry(currentEntry);

                String entryName = currentEntry.getName();
                if (entryName.endsWith(".class") && AccessManipulators.affectedClasses.contains(entryName))
                    outputJar.write(AccessManipulators.transformClass(entryName.replaceAll("\\.class", ""), inputJar.readAllBytes()));
                else
                    outputJar.write(inputJar.readAllBytes());

                currentEntry = inputJar.getNextEntry();
            }

            inputJar.close();
            outputJar.close();

            return transformedFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void transform(TransformOutputs outputs) {
        clearManipulatorCaches();

        for (File manipulatorPath : getParameters().getAccessManipulatorPaths().get()) {
            AccessManipulators.registerModifierFile(manipulatorPath.getPath());
        }

        File input = getInputArtifact().get().getAsFile();
        System.out.println("Transforming File " + input.getName());

        try {
            ZipInputStream inputJar = new ZipInputStream(new FileInputStream(input));
            File transformedFile = outputs.file(input.getName().replaceAll("\\.jar", ".transformed.jar"));
            if (transformedFile.exists()) transformedFile.delete();
            ZipOutputStream outputJar = new ZipOutputStream(new FileOutputStream(
                    transformedFile
            ));

            ZipEntry currentEntry = inputJar.getNextEntry();
            while (currentEntry != null) {
                outputJar.putNextEntry(currentEntry);

                String entryName = currentEntry.getName();
                if (entryName.endsWith(".class") && AccessManipulators.affectedClasses.contains(entryName))
                    outputJar.write(AccessManipulators.transformClass(entryName.replaceAll("\\.class", ""), inputJar.readAllBytes()));
                else
                    outputJar.write(inputJar.readAllBytes());

                currentEntry = inputJar.getNextEntry();
            }

            inputJar.close();
            outputJar.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}