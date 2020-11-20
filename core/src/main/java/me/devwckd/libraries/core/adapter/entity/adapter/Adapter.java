package me.devwckd.libraries.core.adapter.entity.adapter;

/**
 * @author devwckd
 */
public interface Adapter<F, T> {

    T adapt(F from);

    Class<F> getFrom();
    Class<T> getTo();

}
