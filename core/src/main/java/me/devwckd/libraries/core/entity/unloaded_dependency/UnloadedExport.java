package me.devwckd.libraries.core.entity.unloaded_dependency;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.annotation.Import;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

import static java.util.Arrays.*;

/**
 * @author devwckd
 */

@Data
@RequiredArgsConstructor
public class UnloadedExport implements UnloadedDependency {

    private final Method method;
    private final String name;

    @Override
    public Class<?> getDependencyClass() {
        return method.getReturnType();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isNamed() {
        return name != null && !name.isEmpty();
    }

    @Override
    public Object instantiate(Function<Class<?>, Object> loadByClass, Function<String, Object> loadByName)
      throws InstantiationException, InvocationTargetException, IllegalAccessException {
        final Class<?> declaringClass = method.getDeclaringClass();
        final Object declaringClassInstance = loadByClass.apply(declaringClass);
        if(declaringClassInstance == null)
            throw new InstantiationException("Failed to resolve method " + method.getName() + " with the export name of " + name);

        method.setAccessible(true);
        return method.invoke(declaringClassInstance);
    }
}
