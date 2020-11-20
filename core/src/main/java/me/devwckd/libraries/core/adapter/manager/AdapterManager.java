package me.devwckd.libraries.core.adapter.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.adapter.Adapters;
import me.devwckd.libraries.core.adapter.annotation.RegisterAdapter;
import me.devwckd.libraries.core.adapter.entity.adapter.Adapter;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.registry.AdapterRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import sun.jvm.hotspot.runtime.ConstructionException;

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

    private void load() {
        registerAdapters();
    }

    private void registerAdapters() {
        final Reflections reflections = createReflections();
        for (Class<?> adapterClass : reflections.getTypesAnnotatedWith(RegisterAdapter.class)) {
            if(!Adapter.class.isAssignableFrom(adapterClass))
                throw new ClassCastException("Adapter " + adapterClass.getSimpleName() + " is not an instance of Adapter<?, ?>.");

            final Constructor<?> primaryConstructor = adapterClass.getConstructors()[0];
            if(primaryConstructor.getParameters().length > 0) {
                throw new ConstructionException("Adapters cannot have dependencies on their constructor");
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
