package me.devwckd.libraries.core.common.seeker;

import me.devwckd.graph.Graph;
import me.devwckd.graph.impl.HashGraph;
import me.devwckd.libraries.core.entity.loaded_dependency.LoadedDependency;
import me.devwckd.libraries.core.common.seeker.entity.AnalyzedClass;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.stream;

/**
 * @author devwckd
 */

public class HierarchicalDependencySeekerImpl<I, A, R> extends DependencySeekerImpl<I, A, R> implements HierarchicalDependencySeeker<I, A, R> {

    private final Graph<A> graph = new HashGraph<>();
    private final List<Function<A, A[]>> catalogerList = new LinkedList<>();

    public HierarchicalDependencySeekerImpl(String packagePrefix) {
        super(packagePrefix);
    }

    @Override
    public void sort(Function<A, A[]> catalogFunction) {
        catalogerList.add(catalogFunction);
    }

    @Override
    public void seek() {
        analyzeCollection();
        catalogAnalyzedClasses();
        instantiateAnalyzedAndCatalogedClasses();
    }

    protected void catalogAnalyzedClasses() {
        for (A analyzedClass : analyzedClasses) {
            for (Function<A, A[]> cataloger : catalogerList) {
                final A[] analyzedClasses = cataloger.apply(analyzedClass);
                if(analyzedClasses == null) continue;
                graph.addVertex(analyzedClass, analyzedClasses);
            }
        }
    }

    protected void instantiateAnalyzedAndCatalogedClasses() {
        for (A vertex : graph.getVertices()) {
            final List<A> edges = new ArrayList<>(graph.depthFirstTraversal(vertex));
            Collections.reverse(edges);
            for (A edge : edges) {
                final R loadedDependency = instantiator.apply(edge);
                if(loadedDependency == null) continue;
                loadedDependencyList.add(loadedDependency);
            }
        }
    }
}
