package com.github.puzzle.accessmanipulator;

import com.github.puzzle.CosmicPuzzlePlugin;
import com.github.puzzle.extention.PuzzleGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.provider.ListProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;

public abstract class AMPlugin implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AMPlugin.class);

    @Inject
    protected abstract Project getProject();
    @Inject
    protected abstract ConfigurationContainer getConfigurations();

    @Override
    public void run() {
        Attribute<Boolean> manipulated = Attribute.of("manipulated", Boolean.class);

        if (CosmicPuzzlePlugin.EXTENTION.getFabricAccessWidenerPath().isPresent())
            getProject().evaluationDependsOn(CosmicPuzzlePlugin.EXTENTION.getFabricAccessWidenerPath().getAsFile().get().getAbsolutePath());

        if (CosmicPuzzlePlugin.EXTENTION.getForgeAccessTransformerPath().isPresent())
            getProject().evaluationDependsOn(CosmicPuzzlePlugin.EXTENTION.getForgeAccessTransformerPath().getAsFile().get().getAbsolutePath());

        if (CosmicPuzzlePlugin.EXTENTION.getAccessManipulatorPath().isPresent())
            getProject().evaluationDependsOn(CosmicPuzzlePlugin.EXTENTION.getAccessManipulatorPath().getAsFile().get().getAbsolutePath());

        getProject().afterEvaluate(proj -> {

            PuzzleGradleExtension extension = CosmicPuzzlePlugin.EXTENTION;

            proj.getDependencies().getAttributesSchema().attribute(manipulated);
            proj.getDependencies().getArtifactTypes().getByName("jar", artifact -> {
                artifact.getAttributes().attribute(manipulated, false);
            });

            proj.getDependencies().registerTransform(AMClassTransformer.class, param -> {

                param.getFrom().attribute(manipulated, false);
                param.getTo().attribute(manipulated, true);

                ListProperty<File> manipulators = this.getProject().getObjects().listProperty(File.class);
                try {
                    param.parameters(parameters -> {
                        if (extension.getForgeAccessTransformerPath().isPresent())
                            manipulators.add(extension.getForgeAccessTransformerPath().get().getAsFile());
                        if (extension.getFabricAccessWidenerPath().isPresent())
                            manipulators.add(extension.getFabricAccessWidenerPath().get().getAsFile());
                        if (extension.getAccessManipulatorPath().isPresent())
                            manipulators.add(extension.getAccessManipulatorPath().get().getAsFile());

                        parameters.getAccessManipulatorPaths().set(manipulators);
                    });
                } catch (Exception e) {
                    LOGGER.error("Could not read AM file(s)", e);
                    param.getParameters().getAccessManipulatorPaths().set(manipulators);
                }
            });

            getConfigurations().all(config -> {
                if (config.isCanBeResolved())
                    config.getAttributes().attribute(manipulated, true);
            });
        });
    }
}
