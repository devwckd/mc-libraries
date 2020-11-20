package me.devwckd.libraries.core.dependency.registry.dependency;

import me.devwckd.libraries.core.dependency.entity.unloaded_dependency.UnloadedDependency;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * @author devwckd
 */
public class UnloadedDependencyRegistry extends ArrayList<UnloadedDependency> implements DependencyRegistry<UnloadedDependency> {


    @Override
    public UnloadedDependency findByClass(Class<?> clazz) {
        return find(unloadedDependency -> unloadedDependency.getDependencyClass().isAssignableFrom(clazz));
    }

    @Override
    public UnloadedDependency findByName(String name) {
        return find(unloadedDependency -> unloadedDependency.isNamed() && unloadedDependency.getName().equals(name));
    }

    protected UnloadedDependency find(Predicate<UnloadedDependency> predicate) {
        for (UnloadedDependency unloadedDependency : this) {
            if(predicate.test(unloadedDependency))
                return unloadedDependency;
        }

        return null;
    }
}
