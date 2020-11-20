package me.devwckd.libraries.core.dependency.manager;

import me.devwckd.libraries.core.dependency.entity.loaded_dependency.LoadedDependency;
import me.devwckd.libraries.core.dependency.entity.unloaded_dependency.UnloadedDependency;
import me.devwckd.libraries.core.dependency.registry.dependency.LoadedDependencyRegistry;
import me.devwckd.libraries.core.dependency.registry.dependency.UnloadedDependencyRegistry;

/**
 * @author devwckd
 */
public class DependencyManager {

    private final UnloadedDependencyRegistry unloadedDependencies = new UnloadedDependencyRegistry();
    private final LoadedDependencyRegistry loadedDependencies = new LoadedDependencyRegistry();

    public void storeUnloadedDependency(UnloadedDependency unloadedDependency) {
        unloadedDependencies.add(unloadedDependency);
    }

    public void storeLoadedDependency(Object instance) {
        storeLoadedDependency(instance, "");
    }

    public void storeLoadedDependency(Object instance, String name) {
        loadedDependencies.add(new LoadedDependency(instance, name));
    }

    public Object resolveDependencyFromClass(Class<?> clazz) {
        final LoadedDependency loadedDependency = loadedDependencies.findByClass(clazz);
        if(loadedDependency != null) return loadedDependency.getInstance();

        final UnloadedDependency unloadedDependency = unloadedDependencies.findByClass(clazz);
        final Object instance;
        try {
            instance = unloadedDependency.instantiate(this::resolveDependencyFromClass, this::resolveDependencyFromName);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        if(instance != null)
            loadedDependencies.add(new LoadedDependency(instance, ""));

        return instance;
    }

    public Object resolveDependencyFromName(String name) {
        final LoadedDependency loadedDependency = loadedDependencies.findByName(name);
        if(loadedDependency != null) return loadedDependency.getInstance();

        final UnloadedDependency unloadedDependency = unloadedDependencies.findByName(name);
        final Object instance;
        try {
            instance = unloadedDependency.instantiate(this::resolveDependencyFromClass, this::resolveDependencyFromName);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

        if(instance != null)
            loadedDependencies.add(new LoadedDependency(instance, name));

        return instance;
    }

}
