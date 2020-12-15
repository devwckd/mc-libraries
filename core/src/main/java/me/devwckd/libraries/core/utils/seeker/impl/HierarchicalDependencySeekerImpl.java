package me.devwckd.libraries.core.utils.seeker.impl;

import me.devwckd.graph.Graph;
import me.devwckd.graph.impl.HashGraph;
import me.devwckd.libraries.core.utils.seeker.HierarchicalDependencySeeker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.*;
import static java.util.Comparator.*;

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
        final Set<A> vertices = graph.getVertices().stream()
          .sorted(comparingInt(o -> graph.getEdges(o).size()))
          .collect(Collectors.toCollection(LinkedHashSet::new));

        for (A vertex : vertices) {
            final List<A> edges = new ArrayList<>(graph.depthFirstTraversal(vertex));
            reverse(edges);
            for (A edge : edges) {
                final R loadedDependency = instantiator.apply(edge);
                if(loadedDependency == null) continue;
                loadedDependencyList.add(loadedDependency);
            }
        }
    }
}
