package dev.crmodders.puzzle.access_manipulator;

import dev.crmodders.puzzle.access_manipulators.AccessManipulators;
import dev.crmodders.puzzle.access_manipulators.transformers.AccessManipulatorClassWriter;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class AMTransformer implements TransformAction<AMTransformer.parameters> {

    public interface parameters extends TransformParameters {
        @Input @Optional
        Property<String> getAccessManipulator();
        @Input @Optional
        Property<String> getAccessWidener();
        @Input @Optional
        Property<String> getAccessTransformer();
    }

    @InputArtifact
    public abstract Provider<FileSystemLocation> getInputArtifact();

    @Override
    public void transform(@NotNull TransformOutputs outputs) {
        File input = getInputArtifact().get().getAsFile();

        try {
            ZipInputStream inputJar = new ZipInputStream(new FileInputStream(input));
            File outputFile = outputs.file("manipulated-"+ input.getName());
            ZipOutputStream outputJar = new ZipOutputStream(new FileOutputStream(outputFile));

            ZipEntry entry = inputJar.getNextEntry();
            while (entry != null) {
                outputJar.putNextEntry(entry);

                String entryName = entry.getName();
                if (
                                entryName.endsWith(".class") &&
                                AccessManipulators.affectedClasses.contains(entryName)
                ) {
                    outputJar.write(AccessManipulators.transformClass(entryName.replaceAll("\\.class", ""), inputJar.readAllBytes()));
                } else {
                    outputJar.write(inputJar.readAllBytes());
                }

                entry = inputJar.getNextEntry();
            }


            inputJar.close();
            outputJar.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
