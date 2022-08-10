package org.acme;


import java.util.Date;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.redis.datasource.string.StringCommands;

/**
 * This class only implements a synchronized RedisStore for use with HttpCache
 */
public class RedisHttpCacheStore implements StoreBackend<Long, Date>, Consumer<RedisCacheUpdateNotification> {

    private final PubSubCommands<RedisCacheUpdateNotification> pub;
    private final PubSubCommands.RedisSubscriber subscriber;
    private final StringCommands<String, Date> keys;

    private int initalCapacity = 100;

    private int maximumSize = 100;

    /**
     * Local cache, kept updated by
     */
    private final Cache<Long, Date> cache;
    
    /**
     * To use for namespace, should only be non numeric character
     */
    private final String name;

    /**
     *  
     * @param name To use for namespacing, should only contain non numeric characters
     * @param ds
     */
    public RedisHttpCacheStore(final String name, final RedisDataSource ds) {
        this.name = name;
        this.cache = this.makeCache();
        this.keys = ds.string(Date.class);
        this.pub = ds.pubsub(RedisCacheUpdateNotification.class);
        this.subscriber = pub.subscribe(name, this);
    }


    protected Cache<Long, Date> makeCache() {
        return Caffeine.newBuilder()
        .initialCapacity(initalCapacity)
        .maximumSize(maximumSize)
        .build();
    }

    // will update local values everywhere
    @Override
    public void accept(final RedisCacheUpdateNotification notification) {
        // TODO: should filter if it's our own notification 
        this.cache.put(notification.key, notification.value);
    }

    @PreDestroy
    public void terminate() {
        subscriber.unsubscribe(); // Unsubscribe from all subscribed channels
    }


    private String prepareKey(final Long identifier) {
        return this.name + identifier;
    }

    @Override
    public Date get(final Long identifier) {
        final var localGet = this.cache.getIfPresent(identifier);
        if (localGet == null) {
            return this.keys.get(prepareKey(identifier)); // this.name + identifier to namespace keys
        }
        return localGet;
    }

    @Override
    public Date put(final Long identifier, final Date value) {
        this.keys.set(prepareKey(identifier), value);
        // notify others redis
        this.pub.publish(this.name, new RedisCacheUpdateNotification(identifier, value));
        return null; // to be compliant with store definition
    }
}
