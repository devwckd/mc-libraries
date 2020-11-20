package me.devwckd.libraries.core.module.entity.unloaded_dependency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.dependency.entity.unloaded_dependency.UnloadedDependency;
import me.devwckd.libraries.core.module.annotation.Import;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.function.Function;

import static java.util.Arrays.stream;

/**
 * @author devwckd
 */

@Getter
@RequiredArgsConstructor
public class UnloadedModule implements UnloadedDependency {

    private final Class<?> dependencyClass;

    @Override
    public String getName() {
        return dependencyClass.getSimpleName();
    }

    @Override
    public boolean isNamed() {
        return false;
    }

    @Override
    public Object instantiate(Function<Class<?>, Object> loadByClass, Function<String, Object> loadByName)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<?> primaryConstructor = dependencyClass.getConstructors()[0];
        final Object[] objects = stream(primaryConstructor.getParameters())
          .map(parameter -> {
              String name;
              if (parameter.isAnnotationPresent(Import.class) &&
                !(name = parameter.getAnnotation(Import.class).value()).equals("")) {
                  return loadByName.apply(name);
              } else {
                  return loadByClass.apply(parameter.getType());
              }
          })
          .toArray();

        primaryConstructor.setAccessible(true);
        return primaryConstructor.newInstance(objects);
    }

}
