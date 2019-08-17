package gg.manny.spigot.util.compactqueue;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public final class CompactQueue<E>
implements Queue<E>,
Serializable {
    private final ArrayWrapper<E> arrayFactory;
    private final int blockSize;
    private final Deque<ArrayWrapper<E>> blocks;
    private int head;
    private int tail;
    private int highWaterMark;
    private long totalEnqueued;
    private int blocksDisposedOf;
    private static final long serialVersionUID = -8506480751237563952L;

    public CompactQueue(ArrayWrapper<E> arrayFactory, int blockSize) {
        if (arrayFactory == null) {
            throw new IllegalArgumentException("arrayFactory must not be null");
        }
        if (blockSize < 1) {
            throw new IllegalArgumentException("blockSize must be greater than zero");
        }
        this.arrayFactory = arrayFactory;
        this.blockSize = blockSize;
        this.blocks = new ArrayDeque<ArrayWrapper<E>>();
        this.clear();
    }

    private void addABlock() {
        ArrayWrapper<E> block = this.arrayFactory.newInstance(this.blockSize);
        if (block == null) {
            throw new IllegalStateException("could not allocate new block");
        }
        this.blocks.addLast(block);
    }

    private void removeABlock() {
        this.blocks.removeFirst();
        this.head -= this.blockSize;
        this.tail -= this.blockSize;
        ++this.blocksDisposedOf;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            this.add(e);
        }
        if (c.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.blocks.clear();
        this.addABlock();
        this.head = 0;
        this.tail = 0;
        this.highWaterMark = 0;
        this.totalEnqueued = 0;
        this.blocksDisposedOf = 0;
    }

    @Override
    public boolean contains(Object arg0) {
        throw new UnsupportedOperationException("contains() not implemented");
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("containsAll() not implemented");
    }

    @Override
    public boolean isEmpty() {
        if (this.head == this.tail) {
            return true;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("iterator() not implemented");
    }

    @Override
    public boolean remove(Object arg0) {
        throw new UnsupportedOperationException("remove(Object) not implemented");
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("removeAll() not implemented");
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("retainAll() not implemented");
    }

    @Override
    public int size() {
        return this.tail - this.head;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("toArray() not implemented");
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
        throw new UnsupportedOperationException("toArray(T[]) not implemented");
    }

    @Override
    public boolean add(E e) {
        this.blocks.peekLast().set(this.tail % this.blockSize, e);
        ++this.tail;
        if (this.tail > 0 && this.tail % this.blockSize == 0) {
            this.addABlock();
        }
        ++this.totalEnqueued;
        this.highWaterMark = Math.max(this.highWaterMark, this.size());
        return true;
    }

    @Override
    public E element() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        assert (this.head < this.blockSize);
        E r = this.blocks.peek().get(this.head);
        return r;
    }

    @Override
    public boolean offer(E e) {
        return this.add(e);
    }

    @Override
    public E peek() {
        if (this.isEmpty()) {
            return null;
        }
        try {
            return this.element();
        }
        catch (NoSuchElementException noSuchElementException) {
            return null;
        }
    }

    @Override
    public E poll() {
        if (this.isEmpty()) {
            return null;
        }
        try {
            return this.remove();
        }
        catch (NoSuchElementException noSuchElementException) {
            return null;
        }
    }

    @Override
    public E remove() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }
        assert (this.head < this.blockSize);
        E r = this.blocks.peek().get(this.head);
        ++this.head;
        if (this.head == this.blockSize) {
            this.removeABlock();
        }
        if (this.isEmpty()) {
            assert (this.blocks.size() == 1);
            this.head = 0;
            this.tail = 0;
        }
        return r;
    }

    public int getHighWaterMark() {
        return this.highWaterMark;
    }

    public int getBlocksDisposedOf() {
        return this.blocksDisposedOf;
    }

    public int getBlocksInuse() {
        return this.blocks.size();
    }

    public long getTotalElementsEnqueued() {
        return this.totalEnqueued;
    }
}