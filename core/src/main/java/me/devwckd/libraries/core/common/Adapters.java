package me.devwckd.libraries.core.common;

import me.devwckd.libraries.core.entity.adapter.Adapter;
import me.devwckd.libraries.core.registry.AdapterRegistry;

/**
 * @author devwckd
 */
public class Adapters {

    private final AdapterRegistry adapterRegistry = new AdapterRegistry();

    public void storeAdapter(Adapter<?, ?> adapter) {
        adapterRegistry.add(adapter);
    }

    public <F, T> T adapt(F from, Class<T> toClass) {
        final Adapter<F, T> adapter = (Adapter<F, T>) adapterRegistry.findByFromTo(from.getClass(), toClass);
        if(adapter == null) return null;

        return adapter.adapt(from);
    }

    public <F, T> T adaptWithAdapterClass(F from, Class<Adapter<F, T>> adapterClass) {
        final Adapter<F, T> adapter = adapterRegistry.findByAdapterClass(adapterClass);
        if(adapter == null) return null;

        return adapter.adapt(from);
    }

}
