package gg.manny.spigot.util.compactqueue;


public final class ArrayWrapperForPrimitiveLong implements ArrayWrapper<Long> {
	
    private final long[] arr;

    private ArrayWrapperForPrimitiveLong(int length) {
        this.arr = new long[length];
    }

    private ArrayWrapperForPrimitiveLong(long[] arr) {
        this.arr = arr;
    }

    @Override
    public Long get(int index) {
        return this.arr[index];
    }

    @Override
    public void set(int index, Long value) {
        this.arr[index] = value;
    }

    public Object clone() {
        return new ArrayWrapperForPrimitiveLong((long[])this.arr.clone());
    }

    public static ArrayWrapper<Long> factory(int length) {
        return new ArrayWrapperForPrimitiveLong(length);
    }

    @Override
    public ArrayWrapper<Long> newInstance(int length) {
        return new ArrayWrapperForPrimitiveLong(length);
    }

    public ArrayWrapper<Long> newInstance(Long... es) {
    	ArrayWrapper<Long> arr = newInstance(es.length);
    	for (int i = 0; i < es.length; i++) {
    		arr.set(i, es[i]);
    	}
    	return arr;
    }
}