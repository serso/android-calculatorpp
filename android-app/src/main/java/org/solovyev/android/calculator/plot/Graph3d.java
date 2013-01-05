// Copyright (C) 2009-2010 Mihai Preda

package org.solovyev.android.calculator.plot;

import android.graphics.Color;
import org.javia.arity.Function;
import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class Graph3d {

    private final int N;
    private final boolean useHighQuality3d;
    private ShortBuffer verticeIdx;
    private FloatBuffer vertexBuf;
    private ByteBuffer colorBuf;
    private int vertexVbo, colorVbo, vertexElementVbo;
    private boolean useVBO;
    private int nVertex;

    Graph3d(GL11 gl, boolean useHighQuality3d) {
        this.useHighQuality3d = useHighQuality3d;
        this.N = useHighQuality3d ? 36 : 24;

        short[] b = new short[N * N];
        int p = 0;
        for (int i = 0; i < N; i++) {
            short v = 0;
            for (int j = 0; j < N; v += N + N, j += 2) {
                b[p++] = (short) (v + i);
                b[p++] = (short) (v + N + N - 1 - i);
            }
            v = (short) (N * (N - 2));
            i++;
            for (int j = N - 1; j >= 0; v -= N + N, j -= 2) {
                b[p++] = (short) (v + N + N - 1 - i);
                b[p++] = (short) (v + i);
            }
        }
        verticeIdx = buildBuffer(b);

        String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
        useVBO = extensions.indexOf("vertex_buffer_object") != -1;
        //Calculator.log("VBOs support: " + useVBO + " version " + gl.glGetString(GL10.GL_VERSION));

        if (useVBO) {
            int[] out = new int[3];
            gl.glGenBuffers(3, out, 0);
            vertexVbo = out[0];
            colorVbo = out[1];
            vertexElementVbo = out[2];
        }
    }

    private static FloatBuffer buildBuffer(float[] b) {
        ByteBuffer bb = ByteBuffer.allocateDirect(b.length << 2);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer sb = bb.asFloatBuffer();
        sb.put(b);
        sb.position(0);
        return sb;
    }

    private static ShortBuffer buildBuffer(short[] b) {
        ByteBuffer bb = ByteBuffer.allocateDirect(b.length << 1);
        bb.order(ByteOrder.nativeOrder());
        ShortBuffer sb = bb.asShortBuffer();
        sb.put(b);
        sb.position(0);
        return sb;
    }

    private static ByteBuffer buildBuffer(byte[] b) {
        ByteBuffer bb = ByteBuffer.allocateDirect(b.length << 1);
        bb.order(ByteOrder.nativeOrder());
        bb.put(b);
        bb.position(0);
        return bb;
    }

    public void update(@NotNull GL11 gl, @NotNull FunctionPlotDef fpd, float zoom) {
        final Function function = fpd.getFunction();
        final FunctionLineDef lineDef = fpd.getLineDef();
        final int NTICK = useHighQuality3d ? 5 : 0;
        final float size = 4 * zoom;
        final float minX = -size, maxX = size, minY = -size, maxY = size;

        //Calculator.log("update VBOs " + vertexVbo + ' ' + colorVbo + ' ' + vertexElementVbo);
        nVertex = N * N + 6 + 8 + NTICK * 6;

        final float vertices[] = new float[nVertex * 3];
        final byte colors[] = new byte[nVertex * 4];

        if (fpd != null) {
            //Calculator.log("Graph3d update");
            float sizeX = maxX - minX;
            float sizeY = maxY - minY;
            float stepX = sizeX / (N - 1);
            float stepY = sizeY / (N - 1);
            int pos = 0;
            double sum = 0;
            float y = minY;
            float x = minX - stepX;
            int nRealPoints = 0;

            final int arity = function.arity();

            for (int i = 0; i < N; i++, y += stepY) {
                float xinc = (i & 1) == 0 ? stepX : -stepX;

                x += xinc;
                for (int j = 0; j < N; ++j, x += xinc, pos += 3) {

                    final float z;
                    switch (arity) {
                        case 2:
                            z = (float) function.eval(x, y);
                            break;
                        case 1:
                            // todo serso: optimize (can be calculated once before loop)
                            z = (float) function.eval(x);
                            break;
                        case 0:
                            // todo serso: optimize (can be calculated once)
                            z = (float) function.eval();
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }

                    vertices[pos] = x;
                    vertices[pos + 1] = y;
                    vertices[pos + 2] = z;

                    if (!Float.isNaN(z)) {
                        sum += z * z;
                        ++nRealPoints;
                    }
                }
            }

            float maxAbs = (float) Math.sqrt(sum / nRealPoints);
            maxAbs *= .9f;
            maxAbs = Math.min(maxAbs, 15);
            maxAbs = Math.max(maxAbs, .001f);

            final int lineColor = lineDef.getLineColor();
            final int limitColor = N * N * 4;
            for (int i = 0, j = 2; i < limitColor; i += 4, j += 3) {
                final float z = vertices[j];

                    if (!Float.isNaN(z)) {
                        if (lineDef.getLineColorType() == FunctionLineColorType.color_map) {
                            final float a = z / maxAbs;
                            final float abs = a < 0 ? -a : a;
                            colors[i] = floatToByte(a);
                            colors[i + 1] = floatToByte(1 - abs * .3f);
                            colors[i + 2] = floatToByte(-a);
                        } else {
                            colors[i] = (byte) Color.red(lineColor);
                            colors[i + 1] = (byte) Color.green(lineColor);
                            colors[i + 2] = (byte) Color.blue(lineColor);
                        }
                        colors[i + 3] = (byte) 255;
                    } else {
                        vertices[j] = 0;
                        colors[i] = 0;
                        colors[i + 1] = 0;
                        colors[i + 2] = 0;
                        colors[i + 3] = 0;
                    }

            }
        }
        int base = N * N * 3;
        int colorBase = N * N * 4;
        int p = base;
        final int baseSize = 2;
        for (int i = -baseSize; i <= baseSize; i += 2 * baseSize) {
            vertices[p] = i;
            vertices[p + 1] = -baseSize;
            vertices[p + 2] = 0;
            p += 3;
            vertices[p] = i;
            vertices[p + 1] = baseSize;
            vertices[p + 2] = 0;
            p += 3;
            vertices[p] = -baseSize;
            vertices[p + 1] = i;
            vertices[p + 2] = 0;
            p += 3;
            vertices[p] = baseSize;
            vertices[p + 1] = i;
            vertices[p + 2] = 0;
            p += 3;
        }
        for (int i = colorBase; i < colorBase + 8 * 4; i += 4) {
            colors[i] = 0;
            colors[i + 1] = 0;
            colors[i + 2] = (byte) 255;
            colors[i + 3] = (byte) 255;
        }
        base += 8 * 3;
        colorBase += 8 * 4;

        final float unit = 2;
        final float axis[] = {
                0, 0, 0,
                unit, 0, 0,
                0, 0, 0,
                0, unit, 0,
                0, 0, 0,
                0, 0, unit,
        };
        System.arraycopy(axis, 0, vertices, base, 6 * 3);
        for (int i = colorBase; i < colorBase + 6 * 4; i += 4) {
            colors[i] = (byte) 255;
            colors[i + 1] = (byte) 255;
            colors[i + 2] = (byte) 255;
            colors[i + 3] = (byte) 255;
        }
        base += 6 * 3;
        colorBase += 6 * 4;

        p = base;
        final float tick = .03f;
        final float offset = .01f;
        for (int i = 1; i <= NTICK; ++i) {
            vertices[p] = i - tick;
            vertices[p + 1] = -offset;
            vertices[p + 2] = -offset;

            vertices[p + 3] = i + tick;
            vertices[p + 4] = offset;
            vertices[p + 5] = offset;
            p += 6;

            vertices[p] = -offset;
            vertices[p + 1] = i - tick;
            vertices[p + 2] = -offset;

            vertices[p + 3] = offset;
            vertices[p + 4] = i + tick;
            vertices[p + 5] = offset;
            p += 6;

            vertices[p] = -offset;
            vertices[p + 1] = -offset;
            vertices[p + 2] = i - tick;

            vertices[p + 3] = offset;
            vertices[p + 4] = offset;
            vertices[p + 5] = i + tick;
            p += 6;

        }
        for (int i = colorBase + NTICK * 6 * 4 - 1; i >= colorBase; --i) {
            colors[i] = (byte) 255;
        }

        vertexBuf = buildBuffer(vertices);
        colorBuf = buildBuffer(colors);

        if (useVBO) {
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
            gl.glBufferData(GL11.GL_ARRAY_BUFFER, vertexBuf.capacity() * 4, vertexBuf, GL11.GL_STATIC_DRAW);
            vertexBuf = null;

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
            gl.glBufferData(GL11.GL_ARRAY_BUFFER, colorBuf.capacity(), colorBuf, GL11.GL_STATIC_DRAW);
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
            colorBuf = null;

            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, vertexElementVbo);
            gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, verticeIdx.capacity() * 2, verticeIdx, GL11.GL_STATIC_DRAW);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    private byte floatToByte(float v) {
        if (v <= 0) {
            return (byte) 0;
        } else {
            if (v >= 1) {
                return (byte) 255;
            } else {
                return (byte) (v * 255);
            }
        }
    }

    public void draw(GL11 gl) {
        if (useVBO) {
            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexVbo);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, 0);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, colorVbo);
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, 0);

            gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
            // gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, N*N);

            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, vertexElementVbo);
            gl.glDrawElements(GL10.GL_LINE_STRIP, N * N, GL10.GL_UNSIGNED_SHORT, 0);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuf);
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, colorBuf);
            gl.glDrawElements(GL10.GL_LINE_STRIP, N * N, GL10.GL_UNSIGNED_SHORT, verticeIdx);
        }
        final int N2 = N * N;
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, N2);
        gl.glDrawArrays(GL10.GL_LINES, N2, nVertex - N2);
    }

    public boolean isUseHighQuality3d() {
        return useHighQuality3d;
    }
}
