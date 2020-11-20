package me.devwckd.libraries.core.listener.manager;

import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.adapter.annotation.RegisterAdapter;
import me.devwckd.libraries.core.adapter.entity.adapter.Adapter;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.module.annotation.Import;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

/**
 * @author devwckd
 */

@RequiredArgsConstructor
public class ListenerManager {

    private final DependencyManager dependencyManager;
    private final String packagePrefix;

    public void load(Consumer<Object> objectSupplier) {
        final Reflections reflections = createReflections();
        for (Class<?> adapterClass : reflections.getTypesAnnotatedWith(RegisterAdapter.class)) {

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

            try {
                objectSupplier.accept(adapterInstance);
            } catch (ClassCastException exception) {
                exception.printStackTrace();
            }
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
