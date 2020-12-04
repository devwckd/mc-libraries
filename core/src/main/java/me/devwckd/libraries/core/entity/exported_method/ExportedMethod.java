package me.devwckd.libraries.core.entity.exported_method;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.annotation.Module;

import java.lang.reflect.Method;

/**
 * @author devwckd
 */

@Data
public class ExportedMethod {

    private final Method method;
    private final Class<?> exportedClass;
    private final Class<?> exporterClass;
    private final String name;

    public ExportedMethod(Method method) {
        this(method, "");
    }

    public ExportedMethod(Method method, String name) {
        this.method = method;
        this.exportedClass = method.getReturnType();
        this.exporterClass = method.getDeclaringClass();
        this.name = name;
    }

    public boolean isExportedByModule() {
        return exporterClass.isAnnotationPresent(Module.class);
    }

}
