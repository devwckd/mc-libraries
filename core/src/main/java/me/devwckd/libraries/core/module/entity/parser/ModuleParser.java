package me.devwckd.libraries.core.module.entity.parser;

import lombok.RequiredArgsConstructor;
import me.devwckd.graph.Graph;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.module.annotation.Disable;
import me.devwckd.libraries.core.module.annotation.Enable;
import me.devwckd.libraries.core.module.annotation.Load;
import me.devwckd.libraries.core.module.annotation.Module;
import me.devwckd.libraries.core.module.entity.unloaded_dependency.UnloadedModule;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

/**
 * @author devwckd
 */

@RequiredArgsConstructor
public class ModuleParser implements Consumer<Class<?>> {

    private final Graph<Class<?>> moduleGraph;
    private final DependencyManager dependencyManager;

    @Override
    public void accept(Class<?> clazz) {
        dependencyManager.storeUnloadedDependency(new UnloadedModule(clazz));

        final Method[] methods = stream(clazz.getDeclaredMethods())
          .filter(method -> method.isAnnotationPresent(Load.class) ||
              method.isAnnotationPresent(Enable.class) ||
              method.isAnnotationPresent(Disable.class))
          .toArray(Method[]::new);
        if(methods.length < 1) return;

        final Module annotation = clazz.getAnnotation(Module.class);
        moduleGraph.addVertex(clazz, annotation.executeAfter());
        moduleGraph.addVertex(clazz, clazz.getConstructors()[0].getParameterTypes());

    }

}
