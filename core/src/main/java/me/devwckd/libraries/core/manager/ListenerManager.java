package me.devwckd.libraries.core.manager;

import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.annotation.RegisterListener;
import me.devwckd.libraries.core.utils.seeker.DependencySeeker;
import me.devwckd.libraries.core.utils.seeker.impl.DependencySeekerImpl;
import me.devwckd.libraries.core.entity.analyzed_class.AnalyzedClass;
import me.devwckd.libraries.core.entity.loaded_dependency.LoadedDependency;

import java.util.function.Consumer;

import static me.devwckd.libraries.core.utils.DependencyUtils.instantiate;

/**
 * @author devwckd
 */

@RequiredArgsConstructor
public class ListenerManager {

    private final DependencyManager dependencyManager;
    private final String packagePrefix;

    public void load(Consumer<Object> objectSupplier) {
        final DependencySeeker<Class<?>, AnalyzedClass, LoadedDependency> dependencySeeker = new DependencySeekerImpl<>(packagePrefix);
        dependencySeeker.setCollection(reflections -> reflections.getTypesAnnotatedWith(RegisterListener.class));
        dependencySeeker.analyze(clazz -> new AnalyzedClass[] {new AnalyzedClass(clazz, null)});
        dependencySeeker.instantiate(analyzedClass -> instantiate(dependencyManager, analyzedClass.getAnalyzedClass(), LoadedDependency::new));
        dependencySeeker.seek();
        dependencySeeker.consumeResult(loadedDependency -> objectSupplier.accept(loadedDependency.getInstance()));
    }

}
