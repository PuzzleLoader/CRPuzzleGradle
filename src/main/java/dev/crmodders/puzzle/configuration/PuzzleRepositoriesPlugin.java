package dev.crmodders.puzzle.configuration;

import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin;
import dev.crmodders.puzzle.Constants;
import dev.crmodders.puzzle.extention.PuzzleGradleExtension;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.initialization.dsl.ScriptHandler;

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

    public void setupProjectDependencies() {
        // Cosmic Reach
        if (getProject().getProperties().get("cosmic-reach-version") != null) {
            getProject().getDependencies().add("compileOnly", PuzzleGradleExtension.getCosmicReach((String) getProject().getProperties().get("cosmic-reach-version")));
            getProject().getDependencies().add("runtimeOnly", PuzzleGradleExtension.getCosmicReach((String) getProject().getProperties().get("cosmic-reach-version")));
        }

        // Puzzle Loader
        if (getProject().getProperties().get("puzzle-loader-version") != null) {
            getProject().getDependencies().add("compileOnly", PuzzleGradleExtension.getPuzzleLoader((String) getProject().getProperties().get("puzzle-loader-version")));
            getProject().getDependencies().add("runtimeOnly", PuzzleGradleExtension.getPuzzleLoader((String) getProject().getProperties().get("puzzle-loader-version")));
        }

        // Mixins
        getProject().getDependencies().add("compileOnly", "org.spongepowered:mixin:0.8.5");
        getProject().getDependencies().add("runtimeOnly", "org.spongepowered:mixin:0.8.5");
    }
}
