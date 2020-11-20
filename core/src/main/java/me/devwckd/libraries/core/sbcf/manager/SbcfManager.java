package me.devwckd.libraries.core.sbcf.manager;

import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.adapter.annotation.RegisterAdapter;
import me.devwckd.libraries.core.adapter.entity.adapter.Adapter;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.module.annotation.Import;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.*;

/**
 * @author devwckd
 */

@RequiredArgsConstructor
public class SbcfManager {

    private final DependencyManager dependencyManager;
    private final String packagePrefix;

    public void load(Consumer<Object> objectSupplier) {
        final Reflections reflections = createReflections();
        for (Class<?> adapterClass : reflections.getTypesAnnotatedWith(RegisterAdapter.class)) {
            if(!Adapter.class.isAssignableFrom(adapterClass))
                throw new ClassCastException("Adapter " + adapterClass.getSimpleName() + " is not an instance of Adapter<?, ?>.");

            final Constructor<?> primaryConstructor = adapterClass.getConstructors()[0];
            final Object[] objects = stream(primaryConstructor.getParameters())
              .map(parameter -> {
                  String name;
                  if (parameter.isAnnotationPresent(Import.class) &&
                    !(name = parameter.getAnnotation(Import.class).value()).equals("")) {
                      return dependencyManager.resolveDependencyFromName(name);
                  } else {
                      return dependencyManager.resolveDependencyFromClass(parameter.getType());
                  }
              })
              .toArray();

            final Object adapterInstance;
            try {
                adapterInstance = primaryConstructor.newInstance(objects);
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }

            objectSupplier.accept(adapterInstance);
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
