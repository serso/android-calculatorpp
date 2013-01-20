package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ZoomButtonsController;
import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculatorGraph3dView extends GLView implements GraphView {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    private boolean useHighQuality3d = Build.VERSION.SDK_INT >= 5;

    private float lastTouchX, lastTouchY;
    private TouchHandler touchHandler;
    private ZoomButtonsController zoomController = new ZoomButtonsController(this);

    private float zoomLevel = 1;
    private float zoomTarget;
    private float zoomStep = 0;
    private float currentZoom;

    @NotNull
    private List<Graph3d> graphs = new ArrayList<Graph3d>();

    @NotNull
    private GraphViewHelper graphViewHelper = GraphViewHelper.newDefaultInstance();

    @NotNull
    private Graph2dDimensions dimensions = new Graph2dDimensions(this);

    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

    public CalculatorGraph3dView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalculatorGraph3dView(Context context) {
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

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

    protected void onSizeChanged(int w, int h, int ow, int oh) {
        dimensions.setViewDimensions(w, h);
    }

    @Override
    protected void glDraw() {

        if (zoomStep < 0 && zoomLevel > zoomTarget) {
            zoomLevel += zoomStep;
        } else if (zoomStep > 0 && zoomLevel < zoomTarget) {
            zoomLevel += zoomStep;
        } else if (zoomStep != 0) {
            zoomStep = 0;
            zoomLevel = zoomTarget;
            dirty = true;
            if (!shouldRotate()) {
                stopLooping();
            }
        }

        super.glDraw();
    }

    @Override
    public void onDetachedFromWindow() {
        zoomController.setVisible(false);
        super.onDetachedFromWindow();
    }

    // ----

    private float[] matrix1 = new float[16], matrix2 = new float[16], matrix3 = new float[16];
    private float angleX, angleY;
    private boolean dirty;

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
    public void init(@NotNull PlotViewDef plotViewDef) {
        this.graphViewHelper = GraphViewHelper.newInstance(plotViewDef, Collections.<PlotFunction>emptyList());
    }

    @Override
    public void setPlotFunctions(@NotNull List<PlotFunction> plotFunctions) {
        for (PlotFunction plotFunction: plotFunctions) {
            final int arity = plotFunction.getXyFunction().getArity();
            if (arity != 0 && arity != 1 && arity != 2) {
                throw new IllegalArgumentException("Function must have arity 0 or 1 or 2 for 3d plot!");
            }
        }

        this.graphViewHelper = this.graphViewHelper.copy(plotFunctions);
        zoomLevel = 1;
        dirty = true;
    }

    @NotNull
    @Override
    public List<PlotFunction> getPlotFunctions() {
        return this.graphViewHelper.getPlotFunctions();
    }

    @Override
    public void onDestroy() {
        onPause();
    }

    @Override
    public void setXRange(float xMin, float xMax) {
        dimensions.setXRange(PlotBoundaries.DEFAULT_MIN_NUMBER, PlotBoundaries.DEFAULT_MAX_NUMBER);
        dimensions.setXY(dimensions.getX0(), 0);

        zoomLevel = 1;
        dirty = true;
    }

    @Override
    public void setYRange(float yMin, float yMax) {
    }

    @Override
    public float getXMin() {
        return dimensions.getXMin();
    }

    @Override
    public float getXMax() {
        return dimensions.getXMax();
    }

    @Override
    public float getYMin() {
        return dimensions.getYMin();
    }

    @Override
    public float getYMax() {
        return dimensions.getYMax();
    }

    @Override
    public void invalidateGraphs() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSurfaceCreated(GL10 gl, int width, int height) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        final int backgroundColor = graphViewHelper.getPlotViewDef().getBackgroundColor();
        gl.glClearColor(Color.red(backgroundColor) / 255f, Color.green(backgroundColor) / 255f, Color.blue(backgroundColor) / 255f, Color.alpha(backgroundColor) / 255f);

        gl.glShadeModel(useHighQuality3d ? GL10.GL_SMOOTH : GL10.GL_FLAT);
        gl.glDisable(GL10.GL_LIGHTING);
        ensureGraphsSize((GL11) gl);
        dirty = true;
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
        if (dirty) {
            ensureGraphsSize(gl);

            final Graph2dDimensions dimensionsCopy = dimensions.copy();
            dimensionsCopy.setGraphDimensions(dimensions.getGWidth() * zoomLevel / 4, dimensions.getGHeight() * zoomLevel / 4);

            for (int i = 0; i < graphViewHelper.getPlotFunctions().size(); i++) {
                graphs.get(i).update(gl, graphViewHelper.getPlotFunctions().get(i), dimensionsCopy);

            }
            dirty = false;
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

    }

    private void ensureGraphsSize(@NotNull GL11 gl) {
        while (graphViewHelper.getPlotFunctions().size() > graphs.size()) {
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
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < 16; ++i) {
            b.append(m[i]).append(' ');
        }
        //Calculator.log(name + ' ' + b.toString());
    }


    /*
    **********************************************************************
    *
    *                           ZOOM
    *
    **********************************************************************
    */

    public void onVisibilityChanged(boolean visible) {
    }

    public void onZoom(boolean zoomIn) {
        boolean changed = false;
        if (zoomIn) {
            if (canZoomIn(zoomLevel)) {
                zoomTarget = zoomLevel * .625f;
                zoomStep = -zoomLevel / 40;
                changed = true;
            }
        } else {
            if (canZoomOut(zoomLevel)) {
                zoomTarget = zoomLevel * 1.6f;
                zoomStep = zoomLevel / 20;
                changed = true;
            }
        }

        if (changed) {
            zoomController.setZoomInEnabled(canZoomIn(zoomTarget));
            zoomController.setZoomOutEnabled(canZoomOut(zoomTarget));
            if (!shouldRotate()) {
                setRotation(0, 0);
            }
            startLooping();
        }
    }

    private boolean canZoomIn(float zoom) {
        return true;
    }

    private boolean canZoomOut(float zoom) {
        return true;
    }

    /*
    **********************************************************************
    *
    *                           TOUCH
    *
    **********************************************************************
    */

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
    }}
