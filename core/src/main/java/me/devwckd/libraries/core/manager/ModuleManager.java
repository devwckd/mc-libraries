package me.devwckd.libraries.core.manager;

import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.annotation.Export;
import me.devwckd.libraries.core.annotation.Module;
import me.devwckd.libraries.core.common.seeker.DependencySeeker;
import me.devwckd.libraries.core.common.seeker.DependencySeekerImpl;
import me.devwckd.libraries.core.common.seeker.HierarchicalDependencySeeker;
import me.devwckd.libraries.core.common.seeker.HierarchicalDependencySeekerImpl;
import me.devwckd.libraries.core.entity.exported_method.ExportedMethod;
import me.devwckd.libraries.core.entity.loaded_dependency.LoadedDependency;
import me.devwckd.libraries.core.entity.unloaded_dependency.UnloadedExport;
import me.devwckd.libraries.core.utils.ParameterlessPredicate;

import java.lang.reflect.Method;

import static me.devwckd.libraries.core.utils.DependencyUtils.*;

/**
 * @author devwckd
 */

public class ModuleManager {

    private final DependencyManager dependencyManager;
    private final String packagePrefix;
    private final ParameterlessPredicate shutdownTrigger;
    private final ModuleMethodManager moduleMethodManager;

    public ModuleManager(DependencyManager dependencyManager, String packagePrefix, ParameterlessPredicate shutdownTrigger) {
        this.dependencyManager = dependencyManager;
        this.packagePrefix = packagePrefix;
        this.shutdownTrigger = shutdownTrigger;
        this.moduleMethodManager = new ModuleMethodManager(dependencyManager, shutdownTrigger);
    }


    public void init() {
        final DependencySeeker<Method, ExportedMethod, ExportedMethod> exportSeeker = new DependencySeekerImpl<>(packagePrefix);
        exportSeeker.setCollection(reflections -> reflections.getMethodsAnnotatedWith(Export.class));
        exportSeeker.analyze(method -> new ExportedMethod[] { new ExportedMethod(method, method.getAnnotation(Export.class).value())});
        exportSeeker.instantiate(exportedMethod -> exportedMethod.isExportedByModule() ? exportedMethod : null);
        exportSeeker.seek();
        exportSeeker.consumeResult(exportedMethod -> dependencyManager.storeUnloadedDependency(new UnloadedExport(exportedMethod.getMethod(), exportedMethod.getName())));

        final HierarchicalDependencySeeker<Class<?>, Class<?>, LoadedDependency> moduleSeeker = new HierarchicalDependencySeekerImpl<>(packagePrefix);
        moduleSeeker.setCollection(reflections -> reflections.getTypesAnnotatedWith(Module.class));
        moduleSeeker.analyze(clazz -> new Class[] { clazz });
        moduleSeeker.sort(clazz -> clazz.getConstructors()[0].getParameterTypes());
        moduleSeeker.sort(clazz -> clazz.getAnnotation(Module.class).executeAfter());
        moduleSeeker.instantiate(clazz -> {
            moduleMethodManager.checkMethods(clazz);
            return instantiate(dependencyManager, clazz, LoadedDependency::new);
        });
        moduleSeeker.seek();
        moduleSeeker.consumeResult(dependencyManager::storeLoadedDependency);
    }

    public void load() {
        moduleMethodManager.load();
    }

    public void enable() {
        moduleMethodManager.enable();
    }

    public void disable() {
        moduleMethodManager.disable();
    }

    public void reload() {
        moduleMethodManager.reload();
    }

}
