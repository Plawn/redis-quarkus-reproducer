package org.acme;

/**
 * Basic interface to a store class
 */
public interface StoreBackend<I, T> {
    T get(final I identifier);

    T put(final I identifier, final T value);
}
