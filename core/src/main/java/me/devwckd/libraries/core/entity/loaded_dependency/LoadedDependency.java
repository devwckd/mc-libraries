package me.devwckd.libraries.core.entity.loaded_dependency;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author devwckd
 */

@Getter
@RequiredArgsConstructor
public class LoadedDependency {

    private final Object instance;
    private final String name;

    public LoadedDependency(Object instance) {
        this(instance, null);
    }

    public boolean isNamed() {
        return name != null && !name.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadedDependency that = (LoadedDependency) o;
        return instance.equals(that.instance) &&
          Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, name);
    }
}
