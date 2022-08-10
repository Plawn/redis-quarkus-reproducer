package org.acme;


import java.util.Date;

/**
 * Pojo meant to be used for notification with Redis PubSub
 */
public class RedisCacheUpdateNotification {
    public Long key;
    public Date value;

    public RedisCacheUpdateNotification() {

    }

    public RedisCacheUpdateNotification(final Long key, final Date value) {
        this.key = key;
        this.value = value;
    } 
}
