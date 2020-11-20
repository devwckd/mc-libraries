package me.devwckd.libraries.core.dependency.registry.dependency;

/**
 * @author devwckd
 */
public interface DependencyRegistry<T> {

    T findByClass(Class<?> clazz);
    T findByName(String name);

}
