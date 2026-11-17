
package dict;

import java.util.Objects;

public class HashTable<K, V> {
    private static final int MIN_CAPACITY = 16;
    private static final double MAX_LOAD = 0.75;
    private static final double MIN_LOAD = 0.25;

    private static class Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> next;

        // saves the key, value, and next entry
        private Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;

    // starts an empty table
    public HashTable() {
        buckets = newBuckets(MIN_CAPACITY);
    }

    // makes a bucket array of this capacity
    @SuppressWarnings("unchecked")
    private Entry<K, V>[] newBuckets(int capacity) {
        return (Entry<K, V>[]) new Entry<?, ?>[capacity];
    }

    // gets the bucket for a key
    private int index(K key, int capacity) {
        int hash = Objects.hashCode(key);
        hash ^= hash >>> 16;
        return Math.floorMod(hash, capacity);
    }

    // number of entries
    public int size() {
        return size;
    }

    // finds the value for a key, null if missing
    public V get(K key) {
        int bucket = index(key, buckets.length);
        Entry<K, V> entry = buckets[bucket];

        while (entry != null) {
            if (Objects.equals(entry.key, key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    // adds a key or updates its value
    public void put(K key, V value) {
        int bucket = index(key, buckets.length);
        Entry<K, V> entry = buckets[bucket];

        while (entry != null) {
            if (Objects.equals(entry.key, key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }

        if (size + 1 > buckets.length * MAX_LOAD) {
            resize(buckets.length * 2);
            bucket = index(key, buckets.length);
        }

        buckets[bucket] = new Entry<>(key, value, buckets[bucket]);
        size++;
    }

    // removes a key and returns its value, or null
    public V remove(K key) {
        int bucket = index(key, buckets.length);
        Entry<K, V> entry = buckets[bucket];
        Entry<K, V> previous = null;

        while (entry != null) {
            if (Objects.equals(entry.key, key)) {
                if (previous == null) {
                    buckets[bucket] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                size--;

                if (buckets.length > MIN_CAPACITY &&
                        size < buckets.length * MIN_LOAD) {
                    resize(buckets.length / 2);
                }
                return entry.value;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    // rebuilds the table with a new capacity
    private void resize(int capacity) {
        Entry<K, V>[] oldBuckets = buckets;
        buckets = newBuckets(capacity);

        for (Entry<K, V> head : oldBuckets) {
            Entry<K, V> entry = head;
            while (entry != null) {
                Entry<K, V> next = entry.next;
                int bucket = index(entry.key, capacity);
                entry.next = buckets[bucket];
                buckets[bucket] = entry;
                entry = next;
            }
        }
    }
}
