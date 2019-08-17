package gg.manny.spigot.util;

import lombok.Getter;

import java.lang.reflect.Array;

public final class WrappedOverflowArray<T> {

    private T[] data;

    @Getter
    private int currentIndex;

    public WrappedOverflowArray(Class<T> clazz, int capacity) {
        this.data = (T[]) Array.newInstance(clazz, capacity);
    }

    public T get(int position) {
        return this.data[(this.currentIndex - position) % this.data.length];
    }

    public void add(T t) {
        this.currentIndex = (this.currentIndex + 1) % this.data.length;
        this.data[this.currentIndex] = t;
    }
}