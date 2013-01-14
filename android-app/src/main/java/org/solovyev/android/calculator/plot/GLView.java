// Copyright (C) 2009 Mihai Preda

package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

abstract class GLView extends SurfaceView implements SurfaceHolder.Callback {
    private boolean hasSurface;
    private boolean paused;
    private EGL10 egl;
    private EGLDisplay display;
    private EGLConfig config;
    private EGLSurface surface;
    private EGLContext eglContext;
    private GL11 gl;
    protected int width, height;
    private volatile boolean looping;

    abstract void onDrawFrame(GL10 gl);

    abstract void onSurfaceCreated(GL10 gl, int width, int height);

	@NotNull
    public Bitmap captureScreenshot() {
        final Bitmap result = getRawPixels(gl, width, height);
        bitmapBGRtoRGB(result, width, height);
		return result;
    }

    private static Bitmap getRawPixels(GL10 gl, int width, int height) {
        int size = width * height;
        ByteBuffer buf = ByteBuffer.allocateDirect(size * 4);
        buf.order(ByteOrder.nativeOrder());
        gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buf);
        int data[] = new int[size];
        buf.asIntBuffer().get(data);
        buf = null;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(data, size - width, -width, 0, 0, width, height);
        return bitmap;
    }

    @NotNull
    private final Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    glDraw();
                    break;
                default:
                    Log.e("GLView", "Incorrect message id: " + msg.what);
            }
        }
    };

    public GLView(Context context) {
        super(context);
        init();
    }

    public GLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        final SurfaceHolder holder = getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        holder.addCallback(this);
    }

    public void onResume() {
        paused = false;
        if (hasSurface) {
            initGL();
        }
    }

    public void onPause() {
        deinitGL();
    }

    private void initGL() {
        egl = (EGL10) EGLContext.getEGL();
        display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] ver = new int[2];
        egl.eglInitialize(display, ver);

        int[] configSpec = {EGL10.EGL_NONE};
        EGLConfig[] configOut = new EGLConfig[1];
        int[] nConfig = new int[1];
        egl.eglChooseConfig(display, configSpec, configOut, 1, nConfig);
        config = configOut[0];
        eglContext = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, null);
        surface = egl.eglCreateWindowSurface(display, config, getHolder(), null);
        egl.eglMakeCurrent(display, surface, surface, eglContext);
        gl = (GL11) eglContext.getGL();
        onSurfaceCreated(gl, width, height);
        requestDraw();
    }

    private void deinitGL() {
        paused = true;
        if (display != null) {
            egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            egl.eglDestroySurface(display, surface);
            egl.eglDestroyContext(display, eglContext);
            egl.eglTerminate(display);

            egl = null;
            config = null;
            eglContext = null;
            surface = null;
            display = null;
            gl = null;
        }
    }

    protected void glDraw() {
        if (hasSurface && !paused) {
            onDrawFrame(gl);
            if (!egl.eglSwapBuffers(display, surface)) {
            }
            if (egl.eglGetError() == EGL11.EGL_CONTEXT_LOST) {
                paused = true;
            }
            if (looping) {
                requestDraw();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        boolean doInit = !hasSurface && !paused;
        hasSurface = true;
        if (doInit) {
            initGL();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        deinitGL();
    }

    public void startLooping() {
        if (!looping) {
            looping = true;
            glDraw();
        }
    }

    public void stopLooping() {
        if (looping) {
            looping = false;
        }
    }

    public boolean isLooping() {
        return looping;
    }

    public void requestDraw() {
        uiHandler.sendEmptyMessage(1);
    }

    static void bitmapBGRtoRGB(Bitmap bitmap, int width, int height) {
        int size = width * height;
        short data[] = new short[size];
        ShortBuffer buf = ShortBuffer.wrap(data);
        bitmap.copyPixelsToBuffer(buf);
        for (int i = 0; i < size; ++i) {
            //BGR-565 to RGB-565
            short v = data[i];
            data[i] = (short) (((v & 0x1f) << 11) | (v & 0x7e0) | ((v & 0xf800) >> 11));
        }
        buf.rewind();
        bitmap.copyPixelsFromBuffer(buf);
    }
}
