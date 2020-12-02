package me.devwckd.libraries.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devwckd.libraries.core.entity.loaded_dependency.LoadedDependency;
import me.devwckd.libraries.core.manager.DependencyManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static lombok.AccessLevel.*;

/**
 * @author devwckd
 */

@NoArgsConstructor(access = PRIVATE)
public class DependencyUtils {

    public static <T> T instantiate(DependencyManager dependencyManager, Class<?> clazz, Function<Object, T> returnFunction) {
        final Constructor<?> primaryConstructor = clazz.getConstructors()[0];
        final Object[] objects = stream(primaryConstructor.getParameters())
          .map(dependencyManager::resolveDependencyFromParameter)
          .toArray();

        final Object instance;
        try {
            instance = primaryConstructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        return returnFunction.apply(instance);
    }

    public static Object instantiate(DependencyManager dependencyManager, Class<?> clazz) {
        return instantiate(dependencyManager, clazz, object -> object);
    }

}
