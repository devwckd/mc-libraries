package me.devwckd.libraries.core.entity.unloaded_dependency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Arrays.*;

/**
 * @author devwckd
 */

@Getter
@RequiredArgsConstructor
public class UnloadedClass implements UnloadedDependency {

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
          .map(parameter -> loadByClass.apply(parameter.getType()))
          .toArray();

        primaryConstructor.setAccessible(true);
        return primaryConstructor.newInstance(objects);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnloadedClass that = (UnloadedClass) o;
        return dependencyClass.equals(that.dependencyClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependencyClass);
    }
}
