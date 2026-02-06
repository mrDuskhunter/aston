package ru.duskhunter.aston.lesson1;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MyMap<K, V> implements MyMapInterface<K, V> {
    private final static int DEFAULT_CAPACITY = 16;
    private final static double DEFAULT_LOAD_FACTOR = 0.85;
    private int capacity;
    private double loadFactor;
    private int size;
    private Bucket<K, V>[] innerMassive;

    public MyMap() {
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
        if (isEmptyBucket(index)) {
            innerMassive[index] = newBucket;
            size++;
            return;
        } else {
            Bucket<K, V> prevBucket = innerMassive[index];
            boolean isRewrite = checkBucket(prevBucket, newBucket);
            if (isRewrite) {
                innerMassive[index] = newBucket;
                return;
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
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private boolean checkBucket(Bucket<K, V> prevBucket, Bucket<K, V> newBucket) {
        if (newBucket.getHash() == prevBucket.getHash()) { //hash==
            if (prevBucket.equals(newBucket)) { //rewrite
                Bucket<K, V> nextBucket = prevBucket.getNext();
                if (nextBucket != null) {
                    newBucket.setNext(nextBucket);
                }
                return true;
            } else { //hash== && !equals
                ifIsNextBucket(prevBucket, newBucket);
            }
        } else { //hash !=
            ifIsNextBucket(prevBucket, newBucket);
        }
        return false;
    }

    private void ifIsNextBucket(Bucket<K, V> prevBucket, Bucket<K, V> newBucket) { // есть ли следующий баккет? делаем всё тоже самое : вставляем в превБаккет ссылку на новый баккет
        Bucket<K, V> nextBucket = prevBucket.getNext();
        if (nextBucket != null) { //меняем prevBucket = nextBucket и повторяем вызов checkBucket
            prevBucket = nextBucket;
            checkBucket(prevBucket, newBucket);
        } else {
            prevBucket.setNext(newBucket);
        }
    }

    private int getHashKey(K key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private int getIndex(int hash) {
        return hash % capacity;
    }

    private boolean isEmptyBucket(int index) {
        Bucket<?, ?> bucket = innerMassive[index];
        return bucket == null;
    }

    private static class Bucket<K, V> {
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


