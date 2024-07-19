package dev.crmodders.puzzle.accessmanipulator;

import dev.crmodders.puzzle.CosmicPuzzlePlugin;
import dev.crmodders.puzzle.access_manipulators.AccessManipulators;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public abstract class AMPlugin implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AMPlugin.class);

    @Inject
    protected abstract Project getProject();
    @Inject
    protected abstract ConfigurationContainer getConfigurations();

    @Override
    public void run() {
        Attribute<Boolean> manipulated = Attribute.of("manipulated", Boolean.class);

        getProject().

        getProject().afterEvaluate(proj -> {
            PuzzleGradleExtension extension = CosmicPuzzlePlugin.EXTENTION;

            proj.getDependencies().getAttributesSchema().attribute(manipulated);
            proj.getDependencies().getArtifactTypes().getByName("jar", artifact -> {
                artifact.getAttributes().attribute(manipulated, false);
            });


            proj.getDependencies().registerTransform(AMClassTransformer.class, param -> {
                param.getFrom().attribute(manipulated, false);
                param.getTo().attribute(manipulated, true);

                try {

                    List<String> manipulators = new ArrayList<>();

                    param.parameters(parameters -> {
                        if (extension.getForgeAccessTransformerPath().isPresent())
                            manipulators.add(extension.getForgeAccessTransformerPath().get().getAsFile().getAbsolutePath());
                        if (extension.getFabricAccessWidenerPath().isPresent())
                            manipulators.add(extension.getFabricAccessWidenerPath().get().getAsFile().getAbsolutePath());
                        if (extension.getAccessManipulatorPath().isPresent())
                            manipulators.add(extension.getAccessManipulatorPath().get().getAsFile().getAbsolutePath());

                        parameters.getAccessManipulatorPaths().set(manipulators);
                    });
                } catch (Exception e) {
                    LOGGER.error("Could not read AM file(s)", e);
                    param.getParameters().getAccessManipulatorPaths().set(new ArrayList<>());
                }
            });

            getConfigurations().all(config -> {
                if (config.isCanBeResolved())
                    config.getAttributes().attribute(manipulated, true);
            });
        });
    }
}
