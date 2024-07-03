package dev.crmodders.puzzle.access_manipulator;

import dev.crmodders.puzzle.CosmicPuzzlePlugin;
import dev.crmodders.puzzle.access_manipulators.AccessManipulators;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;

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
            if (!(extension.getAccessManipulatorPath().isPresent() || extension.getFabricAccessWidenerPath().isPresent() ||extension.getForgeAccessTransformerPath().isPresent()))
                return;

            proj.getDependencies().getAttributesSchema().attribute(manipulated);
            proj.getDependencies().getArtifactTypes().getByName("jar", artifact -> {
                artifact.getAttributes().attribute(manipulated, false);
            });


            proj.getDependencies().registerTransform(AMTransformer.class, param -> {
                param.getFrom().attribute(manipulated, false);
                param.getTo().attribute(manipulated, true);

                param.parameters(parameters -> {
                    parameters.getAccessTransformer().set("E");
                    parameters.getAccessWidener().set("E");
                    parameters.getAccessTransformer().set("E");
                });

                // Read the access widener file
                try {
                    if (extension.getFabricAccessWidenerPath().isPresent()) AccessManipulators.registerModifierFile(extension.getFabricAccessWidenerPath().getAsFile().get().getPath());
                    if (extension.getForgeAccessTransformerPath().isPresent()) AccessManipulators.registerModifierFile(extension.getForgeAccessTransformerPath().getAsFile().get().getPath());
                    if (extension.getAccessManipulatorPath().isPresent()) AccessManipulators.registerModifierFile(extension.getAccessManipulatorPath().getAsFile().get().getPath());
                } catch (Exception e) {
                    LOGGER.error("Could not read AM file(s)", e);
                }
            });


            // Only transform the Cosmic Reach jar
            // FIXME: Only transform CR jar, rather than every jar
//            Configuration config = getConfigurations().getByName(Constants.Configurations.COSMIC_REACH);
//            if (config.isCanBeResolved()) {
//                System.out.println(config.getName());
//                config.getAttributes().attribute(manipulated, true);
//            }

            getConfigurations().all(config -> {
                if (config.isCanBeResolved())
                    config.getAttributes().attribute(manipulated, true);
            });
        });
    }
}