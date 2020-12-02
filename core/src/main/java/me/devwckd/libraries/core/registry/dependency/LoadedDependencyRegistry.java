package me.devwckd.libraries.core.registry.dependency;

import me.devwckd.libraries.core.entity.loaded_dependency.LoadedDependency;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * @author devwckd
 */
public class LoadedDependencyRegistry extends ArrayList<LoadedDependency> implements DependencyRegistry<LoadedDependency> {

    @Override
    public LoadedDependency findByClass(Class<?> clazz) {
        return find(loadedDependency -> loadedDependency.getInstance().getClass().isAssignableFrom(clazz));
    }

    @Override
    public LoadedDependency findByName(String name) {
        return find(loadedDependency -> loadedDependency.isNamed() && loadedDependency.getName().equals(name));
    }

    protected LoadedDependency find(Predicate<LoadedDependency> predicate) {
        for (LoadedDependency loadedDependency : this) {
            if(predicate.test(loadedDependency))
                return loadedDependency;
        }

        return null;
    }

}
