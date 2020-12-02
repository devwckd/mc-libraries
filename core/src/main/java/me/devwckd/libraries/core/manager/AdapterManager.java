package me.devwckd.libraries.core.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.common.Adapters;
import me.devwckd.libraries.core.annotation.RegisterAdapter;
import me.devwckd.libraries.core.entity.adapter.Adapter;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;

/**
 * @author devwckd
 */

@Getter
@RequiredArgsConstructor
public class AdapterManager {

    private final DependencyManager dependencyManager;
    private final String packagePrefix;

    private final Adapters adapters = new Adapters();

    public void load() {
        registerAdapters();
    }

    private void registerAdapters() {
        final Reflections reflections = createReflections();
        for (Class<?> adapterClass : reflections.getTypesAnnotatedWith(RegisterAdapter.class)) {
            if(!Adapter.class.isAssignableFrom(adapterClass))
                throw new ClassCastException("Adapter " + adapterClass.getSimpleName() + " is not an instance of Adapter<?, ?>.");

            final Constructor<?> primaryConstructor = adapterClass.getConstructors()[0];
            if(primaryConstructor.getParameters().length > 0) {
                throw new IllegalStateException("Adapters cannot have dependencies on their constructor");
            }

            final Object adapterInstance;
            try {
                adapterInstance = primaryConstructor.newInstance();
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }

            adapters.storeAdapter((Adapter<?, ?>) adapterInstance);
        }
    }

    private Reflections createReflections() {
        return new Reflections(
          new ConfigurationBuilder()
            .addScanners(
              new TypeAnnotationsScanner()
            )
            .forPackages(packagePrefix)
        );
    }

}
