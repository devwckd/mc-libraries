package me.devwckd.libraries.core.common.seeker;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.entity.loaded_dependency.LoadedDependency;
import me.devwckd.libraries.core.common.seeker.entity.AnalyzedClass;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * @author devwckd
 */

@Data
@RequiredArgsConstructor
public class DependencySeekerImpl<I, A, R> implements DependencySeeker<I, A, R> {

    protected final String packagePrefix;

    protected final List<Function<I, A[]>> analyzerList = new LinkedList<>();

    protected final List<A> analyzedClasses = new ArrayList<>();
    protected final List<R> loadedDependencyList = new ArrayList<>();

    protected Function<A, R> instantiator;
    protected Collection<I> collection;

    @Override
    public void setCollection(Function<Reflections, Collection<I>> collectionFunction) {
        final Reflections reflections = new Reflections(packagePrefix);
        this.collection = collectionFunction.apply(reflections);
    }

    @Override
    public void analyze(Function<I, A[]> analyzeFunction) {
        analyzerList.add(analyzeFunction);
    }

    @Override
    public void instantiate(Function<A, R> instantiateFunction) {
        instantiator = instantiateFunction;
    }

    @Override
    public void seek() {
        analyzeCollection();
        instantiateAnalyzedClasses();
    }

    @Override
    public Collection<R> getLoadedDependencies() {
        return loadedDependencyList;
    }

    @Override
    public void consumeResult(Consumer<R> loadedDependencyConsumer) {
        loadedDependencyList.forEach(loadedDependencyConsumer);
    }

    protected void analyzeCollection() {
        for (I item : collection) {
            for (Function<I, A[]> analyzer : analyzerList) {
                final A[] result = analyzer.apply(item);
                if (result == null || result.length == 0) continue;
                analyzedClasses.addAll(stream(result).collect(Collectors.toList()));
            }
        }
    }

    protected void instantiateAnalyzedClasses() {
        for (A analyzedClass : analyzedClasses) {
            final R loadedDependency = instantiator.apply(analyzedClass);
            if (loadedDependency == null) continue;
            loadedDependencyList.add(loadedDependency);
        }
    }

}
