// Copyright (C) 2009-2010 Mihai Preda

package org.solovyev.android.calculator.plot;

import android.graphics.Color;
import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

class Graph3d {

    // vertices count per polygon (triangle = 3)
    public static final int VERTICES_COUNT = 3;

    // color components count per color
    public static final int COLOR_COMPONENTS_COUNT = 4;

    // linear polygons count
    private final int n;

    private final boolean useHighQuality3d;
    private ShortBuffer verticeIdx;
    private FloatBuffer vertexBuf;
    private ByteBuffer colorBuf;
    private int vertexVbo, colorVbo, vertexElementVbo;
    private boolean useVBO;
    private int polygonsⁿ;

    Graph3d(GL11 gl, boolean useHighQuality3d) {
        this.useHighQuality3d = useHighQuality3d;
        this.n = useHighQuality3d ? 36 : 24;

        short[] b = new short[n * n];
        int p = 0;
        for (int i = 0; i < n; i++) {
            short v = 0;
            for (int j = 0; j < n; v += n + n, j += 2) {
                b[p++] = (short) (v + i);
                b[p++] = (short) (v + n + n - 1 - i);
            }
            v = (short) (n * (n - 2));
            i++;
            for (int j = n - 1; j >= 0; v -= n + n, j -= 2) {
                b[p++] = (short) (v + n + n - 1 - i);
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

    public void update(@NotNull GL11 gl, @NotNull PlotFunction fpd, float zoom) {
        final XyFunction function = fpd.getXyFunction();
        final PlotLineDef lineDef = fpd.getPlotLineDef();
        final int NTICK = useHighQuality3d ? 5 : 0;

        final float size = 4 * zoom;

        //Calculator.log("update VBOs " + vertexVbo + ' ' + colorVbo + ' ' + vertexElementVbo);
        polygonsⁿ = n * n + 6 + 8 + NTICK * 6;

        // triangle polygon => 3 vertices per polygon
        final float vertices[] = new float[polygonsⁿ * VERTICES_COUNT];

        float maxAbsZ = fillFunctionPolygonVertices(function, size, vertices);
        final byte[] colors = prepareFunctionPolygonColors(lineDef, vertices, maxAbsZ);


        int base = n * n * 3;
        int colorBase = n * n * 4;
        final int baseSize = 2;

        fillBasePolygonVectors(vertices, colors, base, colorBase, baseSize);

        base += 8 * 3;
        colorBase += 8 * 4;

        fillAxisPolygonVectors(vertices, colors, base, colorBase);

        base += 6 * 3;
        colorBase += 6 * 4;

        fillAxisGridPolygonVectors(NTICK, vertices, colors, base, colorBase);

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

    private void fillAxisGridPolygonVectors(int NTICK, float[] vertices, byte[] colors, int base, int colorBase) {
        int p = base;
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
    }

    private void fillAxisPolygonVectors(float[] vertices, byte[] colors, int base, int colorBase) {
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
    }

    private void fillBasePolygonVectors(float[] vertices, byte[] colors, int base, int colorBase, int baseSize) {
        int p = base;
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
            colors[i] = (byte) 255;
            colors[i + 1] = (byte) 255;
            colors[i + 2] = (byte) 255;
            colors[i + 3] = (byte) 255;
        }
    }

    private float fillFunctionPolygonVertices(XyFunction function, float size, float[] vertices) {
        final int arity = function.getArity();

        final float minX = -size;
        final float maxX = size;
        final float minY = -size;
        final float maxY = size;

        float Δx = (maxX - minX) / (n - 1);
        float Δy = (maxY - minY) / (n - 1);

        float y = minY;
        float x = minX - Δx;

        float maxAbsZ = 0;

        float z = 0;
        if (arity == 0) {
            z = (float) function.eval();
        }

        int k = 0;
        for (int i = 0; i < n; i++, y += Δy) {
            float xinc = (i & 1) == 0 ? Δx : -Δx;

            x += xinc;

            if (arity == 1) {
                z = (float) function.eval(y);
            }

            for (int j = 0; j < n; j++, x += xinc, k += VERTICES_COUNT) {

                if (arity == 2) {
                    z = (float) function.eval(y, x);
                }

                vertices[k] = x;
                vertices[k + 1] = y;
                vertices[k + 2] = z;

                if (!Float.isNaN(z)) {
                    final float absZ = Math.abs(z);
                    if (absZ > maxAbsZ) {
                        maxAbsZ = absZ;
                    }
                } else {
                    vertices[k + 2] = 0;
                }
            }
        }

        return maxAbsZ;
    }

    private byte[] prepareFunctionPolygonColors(PlotLineDef lineDef, float[] vertices, float maxAbsZ) {
        // 4 color components per polygon (color[i] = red, color[i+1] = green, color[i+2] = blue, color[i+3] = alpha )
        final byte colors[] = new byte[polygonsⁿ * COLOR_COMPONENTS_COUNT];

        final int lineColor = lineDef.getLineColor();
        final int colorComponentsCount = n * n * COLOR_COMPONENTS_COUNT;
        for (int i = 0, j = VERTICES_COUNT - 1; i < colorComponentsCount; i += COLOR_COMPONENTS_COUNT, j += VERTICES_COUNT) {
            final float z = vertices[j];

            if (!Float.isNaN(z)) {
                if (lineDef.getLineColorType() == PlotLineColorType.color_map) {
                    final float color = z / maxAbsZ;
                    final float abs = Math.abs(color);
                    colors[i] = floatToByte(color);
                    colors[i + 1] = floatToByte(1 - abs * .3f);
                    colors[i + 2] = floatToByte(-color);
                } else {
                    colors[i] = (byte) Color.red(lineColor);
                    colors[i + 1] = (byte) Color.green(lineColor);
                    colors[i + 2] = (byte) Color.blue(lineColor);
                }
                colors[i + 3] = (byte) 255;
            } else {
                colors[i] = 0;
                colors[i + 1] = 0;
                colors[i + 2] = 0;
                colors[i + 3] = 0;
            }
        }
        return colors;
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
            gl.glDrawElements(GL10.GL_LINE_STRIP, n * n, GL10.GL_UNSIGNED_SHORT, 0);
            gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuf);
            gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, colorBuf);
            gl.glDrawElements(GL10.GL_LINE_STRIP, n * n, GL10.GL_UNSIGNED_SHORT, verticeIdx);
        }
        final int N2 = n * n;
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, N2);
        gl.glDrawArrays(GL10.GL_LINES, N2, polygonsⁿ - N2);
    }

    public boolean isUseHighQuality3d() {
        return useHighQuality3d;
    }
}
