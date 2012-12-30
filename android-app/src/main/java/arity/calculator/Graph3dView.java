// Copyright (C) 2009 Mihai Preda

package arity.calculator;

import android.content.Context;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ZoomButtonsController;
import org.javia.arity.Function;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Graph3dView extends GLView implements
        GraphView,
        ZoomButtonsController.OnZoomListener,
        TouchHandler.TouchHandlerInterface {

    private float lastTouchX, lastTouchY;
    private TouchHandler touchHandler;
    private ZoomButtonsController zoomController = new ZoomButtonsController(this);
    private float zoomLevel = 1, targetZoom, zoomStep = 0, currentZoom;
    private FPS fps = new FPS();
    private Graph3d graph;

    public Graph3dView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph3dView(Context context) {
        super(context);
        touchHandler = new TouchHandler(this);
        init();
    }

    private void init() {
        startLooping();
        zoomController.setOnZoomListener(this);

        Matrix.setIdentityM(matrix1, 0);
        Matrix.rotateM(matrix1, 0, -75, 1, 0, 0);
    }

    public void onVisibilityChanged(boolean visible) {
    }

    public void onZoom(boolean zoomIn) {
        boolean changed = false;
        if (zoomIn) {
            if (canZoomIn(zoomLevel)) {
                targetZoom = zoomLevel * .625f;
                zoomStep = -zoomLevel / 40;
                changed = true;
            }
        } else {
            if (canZoomOut(zoomLevel)) {
                targetZoom = zoomLevel * 1.6f;
                zoomStep = zoomLevel / 20;
                changed = true;
            }
        }
        if (changed) {
            zoomController.setZoomInEnabled(canZoomIn(targetZoom));
            zoomController.setZoomOutEnabled(canZoomOut(targetZoom));
            if (!shouldRotate()) {
                setRotation(0, 0);
            }
            startLooping();
        }
    }

    @Override
    protected void glDraw() {
        if ((zoomStep < 0 && zoomLevel > targetZoom) ||
                (zoomStep > 0 && zoomLevel < targetZoom)) {
            zoomLevel += zoomStep;
        } else if (zoomStep != 0) {
            zoomStep = 0;
            zoomLevel = targetZoom;
            isDirty = true;
            if (!shouldRotate()) {
                stopLooping();
            }
        }
        super.glDraw();
    }

    private boolean canZoomIn(float zoom) {
        return zoom > .2f;
    }

    private boolean canZoomOut(float zoom) {
        return zoom < 5;
    }

    @Override
    public void onDetachedFromWindow() {
        zoomController.setVisible(false);
        super.onDetachedFromWindow();
    }

    public void onTouchDown(float x, float y) {
        zoomController.setVisible(true);
        stopLooping();
        lastTouchX = x;
        lastTouchY = y;
    }

    public void onTouchMove(float x, float y) {
        float deltaX = x - lastTouchX;
        float deltaY = y - lastTouchY;
        if (deltaX > 1 || deltaX < -1 || deltaY > 1 || deltaY < -1) {
            setRotation(deltaX, deltaY);
            glDraw();
            lastTouchX = x;
            lastTouchY = y;
        }
    }

    public void onTouchUp(float x, float y) {
        float vx = touchHandler.velocityTracker.getXVelocity();
        float vy = touchHandler.velocityTracker.getYVelocity();
        // Calculator.log("velocity " + vx + ' ' + vy);
        setRotation(vx / 100, vy / 100);
        if (shouldRotate()) {
            startLooping();
        }
    }

    public void onTouchZoomDown(float x1, float y1, float x2, float y2) {

    }

    public void onTouchZoomMove(float x1, float y1, float x2, float y2) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler != null ? touchHandler.onTouchEvent(event) : super.onTouchEvent(event);
    }

    // ----

    private float[] matrix1 = new float[16], matrix2 = new float[16], matrix3 = new float[16];
    private float angleX, angleY;
    private boolean isDirty;
    private Function function;
    private static final float DISTANCE = 15f;

    void setRotation(float x, float y) {
        angleX = x;
        angleY = y;
    }

    boolean shouldRotate() {
        final float limit = .5f;
        return angleX < -limit || angleX > limit || angleY < -limit || angleY > limit;
    }

    public void setFunction(Function f) {
        function = f;
        zoomLevel = 1;
        isDirty = true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, int width, int height) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(0, 0, 0, 1);
        gl.glShadeModel(Calculator.useHighQuality3d ? GL10.GL_SMOOTH : GL10.GL_FLAT);
        gl.glDisable(GL10.GL_LIGHTING);
        graph = new Graph3d((GL11) gl);
        isDirty = true;
        angleX = .5f;
        angleY = 0;

        gl.glViewport(0, 0, width, height);
        initFrustum(gl, DISTANCE * zoomLevel);
        currentZoom = zoomLevel;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GL11 gl = (GL11) gl10;
        if (currentZoom != zoomLevel) {
            initFrustum(gl, DISTANCE * zoomLevel);
            currentZoom = zoomLevel;
        }
        if (isDirty) {
            graph.update(gl, function, zoomLevel);
            isDirty = false;
        }

        if (fps.incFrame()) {
            Calculator.log("f/s " + fps.getValue());
        }

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, -DISTANCE * zoomLevel);

        Matrix.setIdentityM(matrix2, 0);
        float ax = Math.abs(angleX);
        float ay = Math.abs(angleY);
        if (ay * 3 < ax) {
            Matrix.rotateM(matrix2, 0, angleX, 0, 1, 0);
        } else if (ax * 3 < ay) {
            Matrix.rotateM(matrix2, 0, angleY, 1, 0, 0);
        } else {
            if (ax > ay) {
                Matrix.rotateM(matrix2, 0, angleX, 0, 1, 0);
                Matrix.rotateM(matrix2, 0, angleY, 1, 0, 0);
            } else {
                Matrix.rotateM(matrix2, 0, angleY, 1, 0, 0);
                Matrix.rotateM(matrix2, 0, angleX, 0, 1, 0);
            }
        }
        Matrix.multiplyMM(matrix3, 0, matrix2, 0, matrix1, 0);
        gl.glMultMatrixf(matrix3, 0);
        System.arraycopy(matrix3, 0, matrix1, 0, 16);
        graph.draw(gl);
    }

    private void initFrustum(GL10 gl, float distance) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        float near = distance * (1 / 3f);
        float far = distance * 3f;
        float dimen = near / 5f;
        float h = dimen * height / width;
        gl.glFrustumf(-dimen, dimen, -h, h, near, far);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }

    private void printMatrix(float[] m, String name) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < 16; ++i) {
            b.append(m[i]).append(' ');
        }
        Calculator.log(name + ' ' + b.toString());
    }
}
