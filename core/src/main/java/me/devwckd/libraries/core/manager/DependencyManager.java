package me.devwckd.libraries.core.manager;

import me.devwckd.libraries.core.annotation.Import;
import me.devwckd.libraries.core.entity.loaded_dependency.LoadedDependency;
import me.devwckd.libraries.core.entity.unloaded_dependency.UnloadedDependency;
import me.devwckd.libraries.core.registry.dependency.LoadedDependencyRegistry;
import me.devwckd.libraries.core.registry.dependency.UnloadedDependencyRegistry;

import java.lang.reflect.Parameter;

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

    public Object resolveDependencyFromParameter(Parameter parameter) {
        String name;
        if (parameter.isAnnotationPresent(Import.class) &&
          !(name = parameter.getAnnotation(Import.class).value()).equals("")) {
            return resolveDependencyFromName(name);
        } else {
            return resolveDependencyFromClass(parameter.getType());
        }
    }

}
