package me.devwckd.libraries.core.common.seeker;

import me.devwckd.libraries.core.common.seeker.entity.AnalyzedClass;

import java.util.function.Function;

/**
 * @author devwckd
 */
public interface HierarchicalDependencySeeker<I, A, R> extends DependencySeeker<I, A, R>{

    void sort(Function<A, A[]> catalogFunction);

}
