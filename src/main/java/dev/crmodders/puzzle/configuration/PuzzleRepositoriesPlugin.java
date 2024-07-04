package dev.crmodders.puzzle.configuration;

import dev.crmodders.puzzle.Constants;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;

import javax.inject.Inject;

public abstract class PuzzleRepositoriesPlugin implements Runnable {
    @Inject
    protected abstract Project getProject();

    @Override
    public void run() {
        RepositoryHandler repos = getProject().getRepositories();

        setupProjectDependencies();

        repos.ivy(repo -> { // The CR repo
            repo.setName("Cosmic Reach");
            repo.setUrl(Constants.COSMIC_REACH_REPO);

            repo.patternLayout(pattern -> {
                pattern.artifact(Constants.COSMIC_REACH_JAR_NAME);
            });

            repo.metadataSources(sources -> {
                sources.artifact();
            });

            repo.content(content -> {
                content.includeGroup("finalforeach");
            });
        });

        for (String url: Constants.REPOS) { // All other repos
            repos.maven(repo -> {
                repo.setUrl(url);
            });
        }

        repos.mavenCentral();
    }

    public void addImpl(String dep) {
        getProject().getDependencies().add("compileOnly", dep);
        getProject().getDependencies().add("runtimeOnly", dep);
    }

    public void setupProjectDependencies() {
        // Cosmic Reach
        if (getProject().getProperties().get("cosmic-reach-version") != null) {
            addImpl(PuzzleGradleExtension.getCosmicReach((String) getProject().getProperties().get("cosmic-reach-version")));
        }

        // Puzzle Loader
        if (getProject().getProperties().get("puzzle-loader-version") != null) {
            addImpl(PuzzleGradleExtension.getPuzzleLoader((String) getProject().getProperties().get("puzzle-loader-version")));
        }

        // Mixins
        addImpl("org.spongepowered:mixin:0.8.5");

        addImpl("dev.crmodders:access-manipulators:1.0.2");

        // Asm
        addImpl("org.ow2.asm:asm:9.6");
        addImpl("org.ow2.asm:asm-tree:9.6");
        addImpl("org.ow2.asm:asm-util:9.6");
        addImpl("org.ow2.asm:asm-analysis:9.6");
        addImpl("org.ow2.asm:asm-commons:9.6");
    }
}
