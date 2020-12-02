package me.devwckd.libraries.core.entity.parser;

import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.annotation.Export;
import me.devwckd.libraries.core.entity.unloaded_dependency.UnloadedExport;
import me.devwckd.libraries.core.manager.DependencyManager;

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
