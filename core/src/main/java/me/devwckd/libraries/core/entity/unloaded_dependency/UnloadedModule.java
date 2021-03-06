package me.devwckd.libraries.core.entity.unloaded_dependency;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.annotation.Import;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import static java.util.Arrays.stream;

/**
 * @author devwckd
 */

@Data
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
                !(name = parameter.getAnnotation(Import.class).value()).isEmpty()) {
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
