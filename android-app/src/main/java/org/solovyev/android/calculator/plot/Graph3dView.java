// Copyright (C) 2009 Mihai Preda

package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ZoomButtonsController;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.App;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph3dView extends GLView implements GraphView {

    private boolean useHighQuality3d = Build.VERSION.SDK_INT >= 5;

    private float lastTouchX, lastTouchY;
    private TouchHandler touchHandler;
    private ZoomButtonsController zoomController = new ZoomButtonsController(this);
    private float zoomLevel = 1, targetZoom, zoomStep = 0, currentZoom;
    private FPS fps = new FPS();

    @NotNull
    private GLText glText;

    @NotNull
    private List<Graph3d> graphs = new ArrayList<Graph3d>();

    @NotNull
    private GraphViewHelper graphViewHelper = GraphViewHelper.newDefaultInstance();

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
        return true;
    }

    private boolean canZoomOut(float zoom) {
        return true;
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
        float vx = touchHandler.getXVelocity();
        float vy = touchHandler.getYVelocity();
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
        return touchHandler != null ? touchHandler.handleTouchEvent(event) : super.onTouchEvent(event);
    }

    // ----

    private float[] matrix1 = new float[16], matrix2 = new float[16], matrix3 = new float[16];
    private float angleX, angleY;
    private boolean isDirty;
    private static final float DISTANCE = 15f;

    void setRotation(float x, float y) {
        angleX = x;
        angleY = y;
    }

    boolean shouldRotate() {
        final float limit = .5f;
        return angleX < -limit || angleX > limit || angleY < -limit || angleY > limit;
    }

    @Override
    public void init(@NotNull FunctionViewDef functionViewDef) {
        this.graphViewHelper = GraphViewHelper.newInstance(functionViewDef, Collections.<ArityPlotFunction>emptyList());
    }

    public void setFunctionPlotDefs(@NotNull List<ArityPlotFunction> functionPlotDefs) {
        for (ArityPlotFunction functionPlotDef: functionPlotDefs) {
            final int arity = functionPlotDef.getFunction().arity();
            if (arity != 0 && arity != 1 && arity != 2) {
                throw new IllegalArgumentException("Function must have arity 0 or 1 or 2 for 3d plot!");
            }
        }

        this.graphViewHelper = this.graphViewHelper.copy(functionPlotDefs);
        zoomLevel = 1;
        isDirty = true;
    }

	@Override
	public void setXRange(float xMin, float xMax) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

    @Override
    public float getXMin() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getXMax() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getYMin() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getYMax() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSurfaceCreated(GL10 gl, int width, int height) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        final int backgroundColor = graphViewHelper.getFunctionViewDef().getBackgroundColor();
        gl.glClearColor(Color.red(backgroundColor) / 255f, Color.green(backgroundColor) / 255f, Color.blue(backgroundColor) / 255f, Color.alpha(backgroundColor) / 255f);

        gl.glShadeModel(useHighQuality3d ? GL10.GL_SMOOTH : GL10.GL_FLAT);
        gl.glDisable(GL10.GL_LIGHTING);
        ensureGraphsSize((GL11) gl);
        isDirty = true;
        angleX = .5f;
        angleY = 0;

        gl.glViewport(0, 0, width, height);
        initFrustum(gl, DISTANCE * zoomLevel);
        currentZoom = zoomLevel;

        glText = new GLText( gl, App.getInstance().getApplication().getAssets() );
        glText.load("Roboto-Regular.ttf", 32, 2, 2);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GL11 gl = (GL11) gl10;
        if (currentZoom != zoomLevel) {
            initFrustum(gl, DISTANCE * zoomLevel);
            currentZoom = zoomLevel;
        }
        if (isDirty) {
            ensureGraphsSize(gl);
            for (int i = 0; i < graphViewHelper.getFunctionPlotDefs().size(); i++) {
                 graphs.get(i).update(gl, graphViewHelper.getFunctionPlotDefs().get(i), zoomLevel);

            }
            isDirty = false;
        }

        /*if (fps.incFrame()) {
            Calculator.log("f/s " + fps.getValue());
        }*/

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
        for (Graph3d graph : graphs) {
            graph.draw(gl);
        }

        //updateText(gl, glText);

    }

    public void drawText(@NotNull GLText glText) {
        glText.begin(1.0f, 1.0f, 1.0f, 1.0f);
        glText.draw( "Test!", 0, 0 );
        glText.end();
    }

    private void updateText(@NotNull GL11 gl, @NotNull GLText glText) {
        gl.glEnable( GL10.GL_TEXTURE_2D );
        gl.glEnable( GL10.GL_BLEND );
        gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
        drawText(glText);
        gl.glDisable( GL10.GL_BLEND );
        gl.glDisable( GL10.GL_TEXTURE_2D );
    }

    private void ensureGraphsSize(@NotNull GL11 gl) {
        while (graphViewHelper.getFunctionPlotDefs().size() > graphs.size()) {
            graphs.add(new Graph3d(gl, useHighQuality3d));
        }
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
        //Calculator.log(name + ' ' + b.toString());
    }
}
