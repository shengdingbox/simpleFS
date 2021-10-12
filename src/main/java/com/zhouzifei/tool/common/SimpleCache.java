package com.zhouzifei.tool.common;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class SimpleCache<K, V> {
    private final Map<K, V> cache = new WeakHashMap();
    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final ReadLock readLock;
    private final WriteLock writeLock;

    public SimpleCache() {
        this.readLock = this.cacheLock.readLock();
        this.writeLock = this.cacheLock.writeLock();
    }

    public V get(K key) {
        this.readLock.lock();

        Object value;
        try {
            value = this.cache.get(key);
        } finally {
            this.readLock.unlock();
        }

        return (V) value;
    }

    public V put(K key, V value) {
        this.writeLock.lock();

        try {
            this.cache.put(key, value);
        } finally {
            this.writeLock.unlock();
        }

        return value;
    }

    public V remove(K key) {
        this.writeLock.lock();

        Object var2;
        try {
            var2 = this.cache.remove(key);
        } finally {
            this.writeLock.unlock();
        }

        return (V) var2;
    }

    public void clear() {
        this.writeLock.lock();

        try {
            this.cache.clear();
        } finally {
            this.writeLock.unlock();
        }

    }
}
