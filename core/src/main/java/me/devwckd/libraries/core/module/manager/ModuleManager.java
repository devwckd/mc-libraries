package me.devwckd.libraries.core.module.manager;

import lombok.RequiredArgsConstructor;
import me.devwckd.graph.Graph;
import me.devwckd.graph.impl.HashGraph;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.module.annotation.*;
import me.devwckd.libraries.core.module.annotation.Module;
import me.devwckd.libraries.core.module.entity.parser.ExportParser;
import me.devwckd.libraries.core.module.entity.parser.ModuleParser;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.*;

import static java.util.Collections.*;

/**
 * @author devwckd
 */

@RequiredArgsConstructor
public class ModuleManager {

    private final DependencyManager dependencyManager;
    private final String packagePrefix;

    private final Graph<Class<?>> moduleGraph = new HashGraph<>();
    private final LinkedList<Method> loadMethods = new LinkedList<>();
    private final LinkedList<Method> enableMethods = new LinkedList<>();
    private final LinkedList<Method> disableMethods = new LinkedList<>();
    private final LinkedList<Method> reloadMethods = new LinkedList<>();

    public void search() {
        final Reflections reflections = createReflections();

        reflections.getTypesAnnotatedWith(Module.class).forEach(new ModuleParser(moduleGraph, dependencyManager));
        reflections.getMethodsAnnotatedWith(Export.class).forEach(new ExportParser(dependencyManager));
    }

    public void instantiate() {
        final List<Class<?>> checked = new ArrayList<>();
        for (Class<?> vertex : moduleGraph.getVertices()) {
            final List<Class<?>> depthFirstTraversal = new ArrayList<>(moduleGraph.depthFirstTraversal(vertex));
            reverse(depthFirstTraversal);

            for (Class<?> edge : depthFirstTraversal) {
                dependencyManager.resolveDependencyFromClass(edge);
                if(!checked.contains(edge)) {
                    checkMethods(edge);
                    checked.add(edge);
                }
            }
        }
        Collections.reverse(disableMethods);
    }

    public void load() {
        invokeMethods(loadMethods);
    }

    public void enable() {
        invokeMethods(enableMethods);
    }

    public void disable() {
        invokeMethods(disableMethods);
    }

    public void reload() {
        invokeMethods(reloadMethods);
    }

    private void invokeMethods(Collection<Method> methods) {
        for (Method method : methods) {
            final Class<?> declaringClass = method.getDeclaringClass();
            try {
                method.invoke(declaringClass);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void checkMethods(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(Load.class)) loadMethods.add(method);
            if(method.isAnnotationPresent(Enable.class)) enableMethods.add(method);
            if(method.isAnnotationPresent(Disable.class)) disableMethods.add(method);
            if(method.isAnnotationPresent(Reload.class)) reloadMethods.add(method);
        }
    }

    private Reflections createReflections() {
        return new Reflections(
          new ConfigurationBuilder()
            .forPackages(packagePrefix)
            .addScanners(
              new TypeAnnotationsScanner(),
              new MethodAnnotationsScanner()
            )
        );
    }

}
