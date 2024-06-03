package dev.crmodders.puzzle.accesswidener;

import dev.crmodders.puzzle.CosmicPuzzlePlugin;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Files;

public abstract class AWPlugin implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AWPlugin.class);

    @Inject
    protected abstract Project getProject();
    @Inject
    protected abstract ConfigurationContainer getConfigurations();

    @Override
    public void run() {
        Attribute<Boolean> widened = Attribute.of("widened", Boolean.class);


        getProject().afterEvaluate(proj -> {
            PuzzleGradleExtension extension = CosmicPuzzlePlugin.EXTENTION;
            if (!extension.getAccessWidenerPath().isPresent())
                return;


            proj.getDependencies().getAttributesSchema().attribute(widened);
            proj.getDependencies().getArtifactTypes().getByName("jar", artifact -> {
                artifact.getAttributes().attribute(widened, false);
            });


            proj.getDependencies().registerTransform(AWTransformer.class, param -> {
                param.getFrom().attribute(widened, false);
                param.getTo().attribute(widened, true);


                // Read the access widener file
                try {
                    String widener = Files.readString(extension.getAccessWidenerPath().getAsFile().get().toPath());

                    // Pass along the access widener to the transformer
                    param.parameters(parameters -> parameters.getAccessWidener().set(widener));
                } catch (Exception e) {
                    LOGGER.error("Could not read AW file", e);

                    LOGGER.debug("Defaulting to empty AW");
                    param.parameters(parameters -> parameters.getAccessWidener().set(""));
                }
            });


            // Only transform the Cosmic Reach jar
            // FIXME: Only transform CR jar, rather than every jar
//            Configuration config = getConfigurations().getByName(Constants.Configurations.COSMIC_REACH);
//            if (config.isCanBeResolved()) {
//                System.out.println(config.getName());
//                config.getAttributes().attribute(widened, true);
//            }

            getConfigurations().all(config -> {
                if (config.isCanBeResolved())
                    config.getAttributes().attribute(widened, true);
            });
        });
    }
}
