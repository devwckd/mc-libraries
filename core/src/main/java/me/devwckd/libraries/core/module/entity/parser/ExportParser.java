package me.devwckd.libraries.core.module.entity.parser;

import lombok.RequiredArgsConstructor;
import me.devwckd.graph.Graph;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.module.annotation.*;
import me.devwckd.libraries.core.module.annotation.Module;
import me.devwckd.libraries.core.module.entity.unloaded_dependency.UnloadedExport;
import me.devwckd.libraries.core.module.entity.unloaded_dependency.UnloadedModule;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import static java.util.Arrays.stream;

/**
 * @author devwckd
 */

@RequiredArgsConstructor
public class ExportParser implements Consumer<Method> {

    private final DependencyManager dependencyManager;

    @Override
    public void accept(Method method) {
        dependencyManager.storeUnloadedDependency(new UnloadedExport(method, method.getAnnotation(Export.class).value()));
    }

}
