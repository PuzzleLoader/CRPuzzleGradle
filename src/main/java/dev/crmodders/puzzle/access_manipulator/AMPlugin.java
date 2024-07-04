package dev.crmodders.puzzle.access_manipulator;

import dev.crmodders.puzzle.CosmicPuzzlePlugin;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public abstract class AMPlugin implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AMPlugin.class);

    @Inject
    protected abstract Project getProject();
    @Inject
    protected abstract ConfigurationContainer getConfigurations();

    @Override
    public void run() {
        Attribute<Boolean> manipulated = Attribute.of("manipulated", Boolean.class);

        getProject().afterEvaluate(proj -> {
            PuzzleGradleExtension extension = CosmicPuzzlePlugin.EXTENTION;

            proj.getDependencies().getAttributesSchema().attribute(manipulated);
            proj.getDependencies().getArtifactTypes().getByName("jar", artifact -> {
                artifact.getAttributes().attribute(manipulated, false);
            });


            proj.getDependencies().registerTransform(AMTransformer.class, param -> {
                param.getFrom().attribute(manipulated, false);
                param.getTo().attribute(manipulated, true);

                try {
                    param.parameters(parameters -> {
                        if (extension.getForgeAccessTransformerPath().isPresent()) parameters.getForgeAccessTransformerPath().set(extension.getForgeAccessTransformerPath().getAsFile().get().getPath());
                        if (extension.getFabricAccessWidenerPath().isPresent()) parameters.getFabricAccessWidenerPath().set(extension.getFabricAccessWidenerPath().getAsFile().get().getPath());
                        if (extension.getAccessManipulatorPath().isPresent()) parameters.getAccessManipulatorPath().set(extension.getAccessManipulatorPath().getAsFile().get().getPath());
                    });
                } catch (Exception e) {
                    LOGGER.error("Could not read AM file(s)", e);
                }
            });

            getConfigurations().all(config -> {
                if (config.isCanBeResolved())
                    config.getAttributes().attribute(manipulated, true);
            });
        });
    }
}
