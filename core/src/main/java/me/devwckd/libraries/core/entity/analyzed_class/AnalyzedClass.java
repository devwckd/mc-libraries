package me.devwckd.libraries.core.entity.analyzed_class;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author devwckd
 */

@Data
@RequiredArgsConstructor
public class AnalyzedClass {

    private final Class<?> analyzedClass;
    private final String name;

    public boolean isNamed() {
        return name != null && !name.isEmpty();
    }

}
