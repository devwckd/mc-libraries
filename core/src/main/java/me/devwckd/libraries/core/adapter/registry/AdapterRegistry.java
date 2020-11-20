package me.devwckd.libraries.core.adapter.registry;

import me.devwckd.libraries.core.adapter.entity.adapter.Adapter;

import java.util.HashSet;

/**
 * @author devwckd
 */
public class AdapterRegistry extends HashSet<Adapter<?, ?>> {

    public <F, T> Adapter<F, T> findByFromTo(Class<F> fromClass, Class<T> toClass) {
        for (Adapter<?, ?> adapter : this) {
            if (adapter.getFrom().isAssignableFrom(fromClass) && adapter.getTo().isAssignableFrom(toClass))
                return (Adapter<F, T>) adapter;
        }

        return null;
    }

    public <F, T> Adapter<F, T> findByAdapterClass(Class<Adapter<F, T>> adapterClass) {
        for (Adapter<?, ?> adapter : this) {
            if(adapter.getClass().isAssignableFrom(adapterClass))
                return (Adapter<F, T>) adapter;
        }

        return null;
    }

}
