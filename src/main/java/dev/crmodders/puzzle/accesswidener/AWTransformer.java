package dev.crmodders.puzzle.accesswidener;

import dev.crmodders.puzzle.Constants;
import net.fabricmc.accesswidener.AccessWidener;
import net.fabricmc.accesswidener.AccessWidenerClassVisitor;
import net.fabricmc.accesswidener.AccessWidenerReader;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.artifacts.transform.TransformParameters;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class AWTransformer implements TransformAction<AWTransformer.parameters> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWTransformer.class);


    public interface parameters extends TransformParameters {
        @Input
        Property<String> getAccessWidener();
    }

    @InputArtifact
    public abstract Provider<FileSystemLocation> getInputArtifact();

    @Override
    public void transform(TransformOutputs outputs) {
        AccessWidener widener = new AccessWidener();
        AccessWidenerReader reader = new AccessWidenerReader(widener);
        reader.read(getParameters().getAccessWidener().get().getBytes());

        Set<String> targets = widener.getTargets()
                .stream().map(s ->
                        s.replaceAll("\\.", "/") + ".class") // Makes dots into forwared
                .collect(Collectors.toSet());
        File input = getInputArtifact().get().getAsFile();

        try {
            // We are using a Zip(Input|Output)Stream rather than the Jar(Input|Output)Stream as we want to include the non-java class files
            ZipInputStream inputJar = new ZipInputStream(new FileInputStream(input));
            File outputFile = outputs.file("widened-"+ input.getName());
            ZipOutputStream outputJar = new ZipOutputStream(new FileOutputStream(outputFile));

            LOGGER.debug("Transforming ["+ input +"] to ["+ outputFile +"]");


            ZipEntry entry = inputJar.getNextEntry();
            while (entry != null) {
                outputJar.putNextEntry(entry);

                String entryName = entry.getName();
                if (
                                entryName.endsWith(".class") && // Do the quick check of seeing if it's a java file first
                                targets.contains(entryName) // and if it is, then do the slower check of seeing if it should be transformed
                ) {
                    LOGGER.debug("Transforming class: "+ entryName);
                    outputJar.write(transformClass(widener, inputJar.readAllBytes()));
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


    private byte[] transformClass(AccessWidener widener, byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(0);

        ClassVisitor visitor = AccessWidenerClassVisitor.createClassVisitor(Constants.ASM_VERSION, writer, widener);

        reader.accept(visitor, 0);

        return writer.toByteArray();
    }
}
