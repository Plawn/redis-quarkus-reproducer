package org.acme;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.redis.datasource.RedisDataSource;

/**
 * Provides support to inject the redis datasource in 
 * the storeBackends while automiccally namespacing the data
 */
@ApplicationScoped
public class RedisStoreProvider {

    /**
     * Means that you are out a namespace name
     */
    public static class InsufficientNamespaceNamesAvailable extends Exception {

    }

    /**
     * The redis datasource bound for caching
     */
    private final RedisDataSource ds;
    

    public RedisStoreProvider(final RedisDataSource ds) {
        this.ds = ds;
    }
    
    /**
     * Names which will be used for namespacing the caches
     */
    private List<String> namespaceNames = List.of(
            "a", "b", "c", "d", "e", "f", "g", "h", "i",
            "j", "k", "l", "m", "n", "o", "p", "q",
            "r", "s", "t", "u", "v", "w", "x", "y", "z");
    
    /**
     * Iterator to get letter for namespacing in redis
     */
    private final Iterator<String> it = namespaceNames.iterator();



    /**
     * Will throw if no letters to use with namespace
     * 
     * @return
     */
    public RedisHttpCacheStore getStore() throws InsufficientNamespaceNamesAvailable {
        try {
            return new RedisHttpCacheStore(it.next(), ds);
        } catch (final NoSuchElementException e) {
            throw new InsufficientNamespaceNamesAvailable();
        }
    }
}
