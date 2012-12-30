// Copyright (C) 2009 Mihai Preda

package arity.calculator;

class Data {
    float[] xs = new float[4];
    float[] ys = new float[4];
    int size = 0;
    int allocSize = 4;

    void swap(Data o) {
        float savex[] = o.xs;
        float savey[] = o.ys;
        int ssize = o.size;
        int salloc = o.allocSize;

        o.xs = xs;
        o.ys = ys;
        o.size = size;
        o.allocSize = allocSize;
        
        xs = savex;
        ys = savey;
        size = ssize;
        allocSize = salloc;
    }

    void push(float x, float y) {
        if (size >= allocSize) {
            makeSpace(size+1);
        }
        // Calculator.log("push " + size + ' ' + x + ' ' + y);
        xs[size] = x;
        ys[size] = y;
        ++size;
    }

    private void makeSpace(int sizeNeeded) {
        int oldAllocSize = allocSize;
        while (sizeNeeded > allocSize) {
            allocSize += allocSize;
        }
        if (oldAllocSize != allocSize) {
            float[] a = new float[allocSize];
            System.arraycopy(xs, 0, a, 0, size);
            xs = a;
            a = new float[allocSize];
            System.arraycopy(ys, 0, a, 0, size);
            ys = a;
        }
    }

    float topX() {
        return xs[size-1];
    }

    float topY() {
        return ys[size-1];
    }

    float firstX() {
        return xs[0];
    }

    float firstY() {
        return ys[0];
    }

    void pop() {
        --size;
    }

    boolean empty() {
        return size == 0;
    }

    void clear() {
        size = 0;
    }

    void eraseBefore(float x) {
        int pos = 0;
        while (pos < size && xs[pos] < x) {
            ++pos;
        }
        --pos;
        if (pos > 0) {
            size -= pos;
            System.arraycopy(xs, pos, xs, 0, size);
            System.arraycopy(ys, pos, ys, 0, size);
        }
    }

    void eraseAfter(float x) {
        int pos = size-1;
        while (pos >= 0 && x < xs[pos]) {
            --pos;
        }
        ++pos;
        if (pos < size-1) {
            size = pos+1;
        }
    }

    int findPosAfter(float x, float y) {
        int pos = 0;
        while (pos < size && xs[pos] <= x) {
            ++pos;
        }
        if (Float.isNaN(y)) {
            while (pos < size && ys[pos] != ys[pos]) {
                ++pos;
            }
        }
        // Calculator.log("pos " + pos);
        return pos;
    }

    void append(Data d) {
        makeSpace(size + d.size);
        int pos = d.findPosAfter(xs[size-1], ys[size-1]);
        /*
        while (pos < d.size && d.xs[pos] <= last) {
            ++pos;
        }
        if (last != last) {
            while (pos < d.size && d.ys[pos] != d.ys[pos]) {
                ++pos;
            }
        }
        */
        System.arraycopy(d.xs, pos, xs, size, d.size-pos);
        System.arraycopy(d.ys, pos, ys, size, d.size-pos);
        size += d.size-pos;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(size).append(": ");
        for (int i = 0; i < size; ++i) {
            b.append(xs[i]).append(", ");
        }
        return b.toString();
    }
}
