package com.github.puzzle.configuration;

import com.github.puzzle.Constants;
import com.github.puzzle.extention.PuzzleGradleExtension;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;

import javax.inject.Inject;

import java.util.Objects;

import static com.github.puzzle.extention.PuzzleGradleExtension.PUZZLE_VERSION_REFACTOR;

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
                pattern.artifact(Constants.COSMIC_REACH_CLIENT_JAR_NAME);
                pattern.artifact(Constants.COSMIC_REACH_SERVER_JAR_NAME);
            });

            repo.metadataSources(sources -> {
                sources.artifact();
            });

            repo.content(content -> {
                content.includeModule("finalforeach", "cosmicreach");
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
        if (getProject().getProperties().get("cosmic_reach_version") != null) {
            addImpl(PuzzleGradleExtension.getCosmicReach((String) getProject().getProperties().get("cosmic_reach_version")));
        }

        // Puzzle Loader
        if (getProject().getProperties().get("puzzle_loader_version") != null) {
            if (getProject().getProperties().get("puzzle_loader_version").toString().contains("development")) return;
            addImpl(PuzzleGradleExtension.getPuzzleLoader((String) getProject().getProperties().get("puzzle_loader_version")));
        }

        // Puzzle Loader
        if (getProject().getProperties().get("access_manipulators_version") != null) {
            addImpl(PuzzleGradleExtension.getAccessManipulators((String) getProject().getProperties().get("access_manipulators_version")));
        }

        if (getProject().getProperties().get("puzzle_loader_version") != null) {
            if (Objects.equals(getProject().getProperties().get("puzzle_loader_version").toString(), "development-fabric"))
                addImpl("net.fabricmc:sponge-mixin:0.15.3+mixin.0.8.7");
            else if (Objects.equals(getProject().getProperties().get("puzzle_loader_version").toString(), "development-sponge"))
                addImpl("org.spongepowered:mixin:0.8.5");

            // Asm
            addImpl("org.ow2.asm:asm:9.6");
            addImpl("org.ow2.asm:asm-tree:9.6");
            addImpl("org.ow2.asm:asm-util:9.6");
            addImpl("org.ow2.asm:asm-analysis:9.6");
            addImpl("org.ow2.asm:asm-commons:9.6");

            if (getProject().getProperties().get("puzzle_loader_version").toString().contains("development")) return;

            ComparableVersion puzzleVersionString = new ComparableVersion(getProject().getProperties().get("puzzle_loader_version").toString());

            // Mixins
            if (puzzleVersionString.compareTo(PUZZLE_VERSION_REFACTOR) > 0)
                addImpl("net.fabricmc:sponge-mixin:0.15.3+mixin.0.8.7");
            else
                addImpl("org.spongepowered:mixin:0.8.5");
        }

    }
}
