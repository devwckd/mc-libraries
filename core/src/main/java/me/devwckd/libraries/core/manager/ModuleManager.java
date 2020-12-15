package me.devwckd.libraries.core.manager;

import me.devwckd.libraries.core.annotation.Module;
import me.devwckd.libraries.core.annotation.*;
import me.devwckd.libraries.core.entity.exported_method.ExportedMethod;
import me.devwckd.libraries.core.entity.unloaded_dependency.UnloadedExport;
import me.devwckd.libraries.core.utils.ParameterlessPredicate;
import me.devwckd.libraries.core.utils.seeker.DependencySeeker;
import me.devwckd.libraries.core.utils.seeker.HierarchicalDependencySeeker;
import me.devwckd.libraries.core.utils.seeker.impl.DependencySeekerImpl;
import me.devwckd.libraries.core.utils.seeker.impl.HierarchicalDependencySeekerImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static me.devwckd.libraries.core.utils.DependencyUtils.instantiate;

/**
 * @author devwckd
 */

public class ModuleManager {


    private final DependencyManager dependencyManager;
    private final String packagePrefix;
    private final ModuleMethodManager moduleMethodManager;

    public ModuleManager(DependencyManager dependencyManager, String packagePrefix, ParameterlessPredicate shutdownTrigger) {
        this.dependencyManager = dependencyManager;
        this.packagePrefix = packagePrefix;
        this.moduleMethodManager = new ModuleMethodManager(dependencyManager, shutdownTrigger);
    }


    public void init() {
        final DependencySeeker<Method, ExportedMethod, ExportedMethod> exportSeeker = new DependencySeekerImpl<>(packagePrefix);
        exportSeeker.setCollection(reflections -> reflections.getMethodsAnnotatedWith(Export.class));
        exportSeeker.analyze(method -> new ExportedMethod[]{new ExportedMethod(method, method.getAnnotation(Export.class).value())});
        exportSeeker.instantiate(exportedMethod -> exportedMethod.isExportedByModule() ? exportedMethod : null);
        exportSeeker.seek();
        exportSeeker.consumeResult(exportedMethod -> dependencyManager.storeUnloadedDependency(new UnloadedExport(exportedMethod.getMethod(), exportedMethod.getName())));

        final HierarchicalDependencySeeker<Class<?>, Class<?>, Object> moduleSeeker = new HierarchicalDependencySeekerImpl<>(packagePrefix);
        moduleSeeker.setCollection(reflections -> reflections.getTypesAnnotatedWith(Module.class));
        moduleSeeker.analyze(clazz -> new Class[]{clazz});
        moduleSeeker.sort(
          clazz -> stream(clazz.getConstructors()[0].getParameters())
            .map(replaceImportParametersWithModules(exportSeeker))
            .filter(filteredClass -> filteredClass.isAnnotationPresent(Module.class))
            .toArray(Class<?>[]::new)
        );
        moduleSeeker.sort(
          clazz -> stream(clazz.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(Load.class) ||
                method.isAnnotationPresent(Enable.class) ||
                method.isAnnotationPresent(Disable.class))
            .flatMap(method ->
              stream(method.getParameters())
                .map(replaceImportParametersWithModules(exportSeeker))
            ).toArray(Class<?>[]::new)
        );
        moduleSeeker.sort(clazz -> clazz.getAnnotation(Module.class).executeAfter());
        moduleSeeker.instantiate(clazz -> {
            moduleMethodManager.checkMethods(clazz);

            if (dependencyManager.resolveDependencyFromClass(clazz) != null)
                return null;

            final Object instantiate = instantiate(dependencyManager, clazz, o -> o);
            dependencyManager.storeLoadedDependency(instantiate);

            return instantiate;
        });
        moduleSeeker.seek();
    }

    public void loadExports(Object instance) {
        for (Method declaredMethod : instance.getClass().getDeclaredMethods()) {
            if(!declaredMethod.isAnnotationPresent(Export.class)) continue;

            try {
                declaredMethod.setAccessible(true);
                dependencyManager.storeLoadedDependency(declaredMethod.invoke(instance), declaredMethod.getAnnotation(Export.class).value());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
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

    private Function<Parameter, Class<?>> replaceImportParametersWithModules(DependencySeeker<Method, ExportedMethod, ExportedMethod> exportSeeker) {
        return parameter -> {

            if (parameter.isAnnotationPresent(Import.class)) {
                final String name = parameter.getAnnotation(Import.class).value();

                if (!name.isEmpty()) {
                    final Optional<ExportedMethod> exportedMethodOptional = exportSeeker.getResult().stream().filter(
                      exportedMethod -> exportedMethod.getExportedClass().equals(parameter.getType()) &&
                        exportedMethod.getName().equals(name)
                    ).findFirst();
                    if (exportedMethodOptional.isPresent()) return exportedMethodOptional.get().getExporterClass();
                }
            }

            final Optional<ExportedMethod> exportedMethodOptional = exportSeeker.getResult().stream().filter(
              exportedMethod -> exportedMethod.getExportedClass().equals(parameter.getType())
            ).findFirst();
            if (exportedMethodOptional.isPresent()) return exportedMethodOptional.get().getExporterClass();

            return parameter.getType();
        };
    }

}
