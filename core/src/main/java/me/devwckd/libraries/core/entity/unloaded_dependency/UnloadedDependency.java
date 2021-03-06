package me.devwckd.libraries.core.entity.unloaded_dependency;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

/**
 * @author devwckd
 */

public interface UnloadedDependency {

    Class<?> getDependencyClass();

    String getName();
    boolean isNamed();

    Object instantiate(Function<Class<?>, Object> loadByClass, Function<String, Object> loadByName) throws IllegalAccessException, InvocationTargetException, InstantiationException;

}
