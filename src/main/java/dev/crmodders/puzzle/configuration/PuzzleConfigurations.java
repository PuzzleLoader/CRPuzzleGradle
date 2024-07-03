package dev.crmodders.puzzle.configuration;

import dev.crmodders.puzzle.Constants;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.artifacts.configurations.Configurations;

import javax.inject.Inject;

public abstract class PuzzleConfigurations implements Runnable {
    @Inject
    protected abstract ConfigurationContainer getConfigurations();


    @Override
    public void run() {
        Configuration BundleConfiguration = register("bundle", Role.RESOLVABLE).get();
        getConfigurations().getByName("api").extendsFrom(BundleConfiguration);

        Configuration ModConfiguration = registerInvisible("mod", Role.NONE).get();
        getConfigurations().add(ModConfiguration);

        Configuration InternalConfiguration = registerInvisible("internal", Role.NONE).get();
        getConfigurations().add(InternalConfiguration);
    }



    private NamedDomainObjectProvider<Configuration> register(String name, Role role) {
        return getConfigurations().register(name, role::apply);
    }

    private NamedDomainObjectProvider<Configuration> registerInvisible(String name, Role role) {
        final NamedDomainObjectProvider<Configuration> provider = register(name, role);
        provider.configure(configuration -> configuration.setVisible(false));
        return provider;
    }

    enum Role {
        NONE(false, false),
        CONSUMABLE(true, false),
        RESOLVABLE(false, true);

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
