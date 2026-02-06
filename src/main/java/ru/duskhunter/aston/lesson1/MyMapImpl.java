package ru.duskhunter.aston.lesson1;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MyMapImpl<K, V> implements MyMap<K, V> {
    private final static int DEFAULT_CAPACITY = 16;
    private final static double DEFAULT_LOAD_FACTOR = 0.85;
    private int capacity;
    private double loadFactor;
    private int size;
    private Bucket<K, V>[] innerMassive;

    public MyMapImpl() {
        this.capacity = DEFAULT_CAPACITY;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.size = 0;
        this.innerMassive = new Bucket[capacity];
    }

    @Override
    public void put(K key, V value) {
        int hash = getHashKey(key);
        int index = getIndex(hash);
        Bucket<K, V> newBucket = new Bucket<>(key, value, hash);
        Bucket<K, V> currentBucket = innerMassive[index];
        if (currentBucket == null) {
            innerMassive[index] = newBucket;
            size++;
            return;
        } else {
            boolean isRewrite;
            Bucket<K, V> prevBucket = null;
            do {
                isRewrite = checkBucket(prevBucket, currentBucket, newBucket);
                if (isRewrite) {
                    break;
                }
                prevBucket = currentBucket;
                currentBucket = currentBucket.getNext();
            } while (currentBucket != null);

            if (isRewrite) {
                innerMassive[index] = newBucket;
                return;
            } else {
                prevBucket.setNext(newBucket);
            }
        }
        size++;
    }

    @Override
    public V get(K key) {
        int hash = getHashKey(key);
        int index = getIndex(hash);
        Bucket<K, V> bucket = innerMassive[index];
        if (bucket == null) {
            return null;
        } else {
            return bucket.stream()
                    .filter(b -> b.getHash() == hash)
                    .filter(b -> Objects.equals(b.getKey(), key))
                    .map(Bucket::getValue)
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public V remove(K key) {
        int hash = getHashKey(key);
        int index = getIndex(hash);
        Bucket<K, V> targetBucket = new Bucket<>(key, null, hash);

        Bucket<K, V> currentBucket = innerMassive[index];
        if (currentBucket != null) {
            int isMatch;
            Bucket<K, V> prevBucket = null;
            do {
                isMatch = checkBucketRemove(prevBucket, currentBucket, targetBucket);
                if (isMatch > 0) {
                    size--;
                    break;
                }
                prevBucket = currentBucket;
                currentBucket = currentBucket.getNext();
            } while (currentBucket != null);

            if (isMatch == 2) {
                innerMassive[index] = currentBucket.getNext();
                currentBucket.setNext(null);
            }
            return isMatch == 0 ? null : currentBucket.getValue();
        } else return null;
    }


    @Override
    public int size() {
        return size;
    }

    private boolean checkBucket(Bucket<K, V> prevBucket, Bucket<K, V> currentBucket, Bucket<K, V> newBucket) {
        if (newBucket.getHash() == currentBucket.getHash() && currentBucket.equals(newBucket)) { //rewrite
            Bucket<K, V> nextBucket = currentBucket.getNext();
            if (nextBucket != null) {
                newBucket.setNext(nextBucket);
            }
            if (prevBucket != null) {
                prevBucket.setNext(newBucket);
            }
            return true;
        }
        return false;
    }

    private int checkBucketRemove(Bucket<K, V> prevBucket, Bucket<K, V> currentBucket, Bucket<K, V> removeBucket) {
        if (removeBucket.getHash() == currentBucket.getHash() && currentBucket.equals(removeBucket)) { //rewrite
            Bucket<K, V> nextBucket = currentBucket.getNext();
            if (prevBucket != null && nextBucket != null) {
                prevBucket.setNext(nextBucket);
            } else if (prevBucket != null) {
                prevBucket.setNext(null);
            } else {
                return 2;
            }
            return 1;
        }
        return 0;
    }

    private int getHashKey(K key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private int getIndex(int hash) {
        return hash % capacity;
    }

    private class Bucket<K, V> {
        private final K key;
        private final V value;
        private final int hash;
        private Bucket<K, V> next;

        public Bucket(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }

        public V getValue() {
            return value;
        }

        public K getKey() {
            return key;
        }

        public int getHash() {
            return hash;
        }

        public Bucket<K, V> getNext() {
            return next;
        }

        public void setNext(Bucket<K, V> next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bucket<?, ?> bucket = (Bucket<?, ?>) o;
            return Objects.equals(key, bucket.key);
        }

        @Override
        public int hashCode() {
            int h;
            return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        }

        public void forEach(Consumer<V> action) {
            Objects.requireNonNull(action);
            Bucket<K, V> current = this;
            while (current != null) {
                action.accept(current.value);
                current = current.next;
            }
        }

        public void forEach(BiConsumer<K, V> action) {
            Objects.requireNonNull(action);
            Bucket<K, V> current = this;
            while (current != null) {
                action.accept(current.key, current.value);
                current = current.next;
            }
        }

        public Stream<Bucket<K, V>> stream() {
            return Stream.iterate(this, Objects::nonNull, Bucket::getNext);
        }
    }
}