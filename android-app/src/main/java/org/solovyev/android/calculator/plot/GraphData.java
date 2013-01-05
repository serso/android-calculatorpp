// Copyright (C) 2009 Mihai Preda

package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

class GraphData {

    private int size = 0;

    private int allocatedSize = 4;
    private float[] xs = new float[allocatedSize];
    private float[] ys = new float[allocatedSize];

    private GraphData() {
    }

    @NotNull
    static GraphData newEmptyInstance() {
        return new GraphData();
    }

    void swap(@NotNull GraphData that) {
        float savedXs[] = that.xs;
        float savedYs[] = that.ys;
        int savedSize = that.size;
        int savedAllocatedSize = that.allocatedSize;

        that.xs = this.xs;
        that.ys = this.ys;
        that.size = this.size;
        that.allocatedSize = this.allocatedSize;

        this.xs = savedXs;
        this.ys = savedYs;
        this.size = savedSize;
        this.allocatedSize = savedAllocatedSize;
    }

    void push(float x, float y) {
        if (size >= allocatedSize) {
            makeSpace(size + 1);
        }

        xs[size] = x;
        ys[size] = y;
        ++size;
    }

    private void makeSpace(int spaceSize) {
        int oldAllocatedSize = allocatedSize;
        while (spaceSize > allocatedSize) {
            allocatedSize += allocatedSize;
        }

        if (oldAllocatedSize != allocatedSize) {
            float[] a = new float[allocatedSize];
            System.arraycopy(xs, 0, a, 0, this.size);
            xs = a;
            a = new float[allocatedSize];
            System.arraycopy(ys, 0, a, 0, this.size);
            ys = a;
        }
    }

    float topX() {
        return xs[size - 1];
    }

    float topY() {
        return ys[size - 1];
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
        int pos = size - 1;
        while (pos >= 0 && x < xs[pos]) {
            --pos;
        }
        ++pos;
        if (pos < size - 1) {
            size = pos + 1;
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

    void append(GraphData d) {
        makeSpace(size + d.size);
        int pos = d.findPosAfter(xs[size - 1], ys[size - 1]);
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
        System.arraycopy(d.xs, pos, xs, size, d.size - pos);
        System.arraycopy(d.ys, pos, ys, size, d.size - pos);
        size += d.size - pos;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(size).append(": ");
        for (int i = 0; i < size; ++i) {
            b.append(xs[i]).append(", ");
        }
        return b.toString();
    }

    public float[] getXs() {
        return xs;
    }

    public float[] getYs() {
        return ys;
    }

    public int getSize() {
        return size;
    }
}
