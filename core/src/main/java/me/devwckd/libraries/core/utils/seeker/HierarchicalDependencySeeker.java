package me.devwckd.libraries.core.utils.seeker;

import java.util.function.Function;

/**
 * @author devwckd
 */
public interface HierarchicalDependencySeeker<I, A, R> extends DependencySeeker<I, A, R>{

    void sort(Function<A, A[]> catalogFunction);

}
