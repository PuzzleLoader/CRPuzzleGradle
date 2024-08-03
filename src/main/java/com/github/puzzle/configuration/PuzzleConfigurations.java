package com.github.puzzle.configuration;

import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;

import javax.inject.Inject;

public abstract class  PuzzleConfigurations implements Runnable {
    @Inject
    protected abstract ConfigurationContainer getConfigurations();

    @Override
    public void run() {
        Configuration CompileOnly = getConfigurations().getByName("compileOnly");

        Configuration BundleConfiguration = register("bundle", Role.CONSUMEANDRESOLE).get();
        BundleConfiguration.setDescription("Make this dependency included in your mod jar");
        getConfigurations().getByName("implementation").extendsFrom(BundleConfiguration);

        Configuration ModConfiguration = registerInvisible("mod", Role.NONE).get();
        ModConfiguration.setDescription("This dependency is considered a mod");
        CompileOnly.extendsFrom(ModConfiguration);

        Configuration InternalConfiguration = registerInvisible("internal", Role.NONE).get();
        CompileOnly.extendsFrom(InternalConfiguration);
    }



    private NamedDomainObjectProvider<Configuration> register(String name, Role role) {
        return getConfigurations().register(name, role::apply);
    }

    private NamedDomainObjectProvider<Configuration> registerInvisible(String name, Role role) {
        final NamedDomainObjectProvider<Configuration> provider = register(name, role);
        provider.configure(configuration -> configuration.setVisible(false));
        return provider;
    }

    private NamedDomainObjectProvider<Configuration> registerTransitive(String name, Role role) {
        final NamedDomainObjectProvider<Configuration> provider = register(name, role);
        provider.configure(configuration -> configuration.setTransitive(false));
        return provider;
    }

    enum Role {
        NONE(false, false),
        CONSUMABLE(true, false),
        RESOLVABLE(false, true),
        CONSUMEANDRESOLE(true, true);

        private final boolean canBeConsumed;
        private final boolean canBeResolved;

        Role(boolean canBeConsumed, boolean canBeResolved) {
            this.canBeConsumed = canBeConsumed;
            this.canBeResolved = canBeResolved;
        }

        void apply(Configuration configuration) {
            configuration.setCanBeConsumed(canBeConsumed);
            configuration.setCanBeResolved(canBeResolved);
        }
    }
}
