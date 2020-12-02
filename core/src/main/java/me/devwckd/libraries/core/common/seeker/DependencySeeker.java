package me.devwckd.libraries.core.common.seeker;

import org.reflections.Reflections;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author devwckd
 */
public interface DependencySeeker<I, A, R> {

    String getPackagePrefix();

    void setCollection(Function<Reflections, Collection<I>> collectionFunction);
    Collection<I> getCollection();

    void analyze(Function<I, A[]> analyzeFunction);
    void instantiate(Function<A, R> instantiateClass);

    void seek();

    Collection<R> getLoadedDependencies();
    void consumeResult(Consumer<R> loadedDependencyConsumer);

}
