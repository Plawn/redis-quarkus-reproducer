package org.acme;

import javax.enterprise.context.ApplicationScoped;

import org.acme.RedisStoreProvider.InsufficientNamespaceNamesAvailable;

import io.quarkus.runtime.Startup;

/**
 * This class produces the httpCacheHandlers for the app, they are used to keep track of the
 * last-modified date in order to provide 304 Not Modified handling on the client side <br>
 * This enables the server to send less data over the wire and to keep the data consumption of the
 * client low
 */
@Startup
@ApplicationScoped
public class HttpCacheProvider {
    
    public HttpCacheProvider(final RedisStoreProvider redisProvider) throws InsufficientNamespaceNamesAvailable {
        redisProvider.getStore();
        redisProvider.getStore();
        redisProvider.getStore();
        redisProvider.getStore();
        redisProvider.getStore();
        redisProvider.getStore(); // until here it works
        redisProvider.getStore(); // now we have a timeout
        // redisProvider.getStore();
        // redisProvider.getStore();
    }

}
