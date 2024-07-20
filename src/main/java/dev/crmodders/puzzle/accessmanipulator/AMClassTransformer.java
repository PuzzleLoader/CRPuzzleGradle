package dev.crmodders.puzzle.accessmanipulator;

import dev.crmodders.puzzle.access_manipulators.AccessManipulators;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;

import java.io.*;
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

    @Override
    public void transform(TransformOutputs outputs) {
        File input = getInputArtifact().get().getAsFile();

        clearManipulatorCaches();

        for (File manipulatorPath : getParameters().getAccessManipulatorPaths().get()) {
            AccessManipulators.registerModifierFile(manipulatorPath.getPath());
        }

        try {
            ZipInputStream inputJar = new ZipInputStream(new FileInputStream(input));
            File transformedFile = outputs.file("manipulated-"+input.getName());
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
