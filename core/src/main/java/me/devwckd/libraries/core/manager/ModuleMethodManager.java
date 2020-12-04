package me.devwckd.libraries.core.manager;

import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.annotation.Disable;
import me.devwckd.libraries.core.annotation.Enable;
import me.devwckd.libraries.core.annotation.Load;
import me.devwckd.libraries.core.annotation.Reload;
import me.devwckd.libraries.core.utils.ParameterlessPredicate;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.*;

/**
 * @author devwckd
 */

@RequiredArgsConstructor
public class ModuleMethodManager {

    private final DependencyManager dependencyManager;
    private final ParameterlessPredicate shutdownTrigger;

    private final List<Class<?>> checkedClasses = new ArrayList<>();

    private final List<Method> loadMethods = new LinkedList<>();
    private final List<Method> enableMethods = new LinkedList<>();
    private final List<Method> disableMethods = new LinkedList<>();
    private final List<Method> reloadMethods = new LinkedList<>();

    public void checkMethods(Class<?> clazz) {
        if(checkedClasses.contains(clazz)) return;

        for (Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(Load.class)) loadMethods.add(method);
            if(method.isAnnotationPresent(Enable.class)) enableMethods.add(method);
            if(method.isAnnotationPresent(Disable.class)) disableMethods.add(method);
            if(method.isAnnotationPresent(Reload.class)) reloadMethods.add(method);
        }

        checkedClasses.add(clazz);
    }

    public void load() {
        invokeMethods(loadMethods);
    }

    public void enable() {
        invokeMethods(enableMethods);
    }

    public void disable() {
        invokeMethods(disableMethods, false);
    }

    public void reload() {
        invokeMethods(reloadMethods);
    }

    private void invokeMethods(Collection<Method> methods, boolean useTrigger) {
        for (Method method : methods) {
            if(useTrigger && shutdownTrigger.test()) return;

            final Object declaringInstance = dependencyManager.resolveDependencyFromClass(method.getDeclaringClass());
            final Object[] objects = stream(method.getParameters())
              .map(dependencyManager::resolveDependencyFromParameter)
              .toArray();

            try {
                method.setAccessible(true);
                method.invoke(declaringInstance, objects);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void invokeMethods(Collection<Method> methods) {
        invokeMethods(methods, true);
    }

}
