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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class AMTransformer implements TransformAction<AMTransformer.parameters> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AMTransformer.class);


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
    public void transform(TransformOutputs outputs) {
        File input = getInputArtifact().get().getAsFile();

        try {
            ZipInputStream inputJar = new ZipInputStream(new FileInputStream(input));
            File outputFile = outputs.file("manipulated-"+ input.getName());
            ZipOutputStream outputJar = new ZipOutputStream(new FileOutputStream(outputFile));

            LOGGER.info("Transforming ["+ input +"] to ["+ outputFile +"]");


            ZipEntry entry = inputJar.getNextEntry();
            while (entry != null) {
                outputJar.putNextEntry(entry);

                String entryName = entry.getName();
                LOGGER.info("Transforming class: " + entryName + " " + AccessManipulators.affectedClasses);
                if (
                                entryName.endsWith(".class") && // Do the quick check of seeing if it's a java file first
                                AccessManipulators.affectedClasses.contains(entryName) // and if it is, then do the slower check of seeing if it should be transformed
                ) {
                    LOGGER.info("Transforming class: " + entryName);
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
