// Copyright (C) 2009-2010 Mihai Preda

package org.solovyev.android.calculator.plot;


import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import android.widget.ZoomButtonsController;
import org.javia.arity.Function;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculatorGraph2dView extends View implements GraphView {

    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    private static final float TICKS_COUNT = 15;
    public static final int TICK_SIZE_PXS = 3;

    private static final DecimalFormat tickFormat = new DecimalFormat("##0.#####E0");
    private static final int MAX_TICK_DIGITS = 4;
    private static final String[] TICK_FORMATS = new String[MAX_TICK_DIGITS];
    static {
        for(int i = 0; i < MAX_TICK_DIGITS; i++) {
            TICK_FORMATS[i] = "%." + i + "f";
        }
    }
    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    // view width and height in pixels
    private int widthPxs;
    private int heightPxs;

    @NotNull
    private final Matrix matrix = new Matrix();

    // paints

    @NotNull
    private final Paint paint = new Paint();

    @NotNull
    private final Paint textPaint = new Paint();

    @NotNull
    private final Paint fillPaint = new Paint();

    @NotNull
    private GraphViewHelper graphViewHelper = GraphViewHelper.newDefaultInstance();

    private final GraphData next = GraphData.newEmptyInstance();

    private final GraphData endGraph = GraphData.newEmptyInstance();

    @NotNull
    private List<GraphData> graphs = new ArrayList<GraphData>(graphViewHelper.getFunctionPlotDefs().size());

    // current position of camera in graph coordinates
    private float x0;
    private float y0;

    // graph width in function units (NOT screen pixels)
    private float graphWidth = 20;

    private float lastXMin;

    private float lastYMin;
    private float lastYMax;

    private float lastTouchX, lastTouchY;


    @NotNull
    private TouchHandler touchHandler;

    @NotNull
    protected ZoomButtonsController zoomController = new ZoomButtonsController(this);

    @NotNull
    private ZoomTracker zoomTracker = new ZoomTracker();

    @NotNull
    private Scroller scroller;

    public CalculatorGraph2dView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalculatorGraph2dView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        touchHandler = new TouchHandler(this);
        zoomController.setOnZoomListener(this);
        scroller = new Scroller(context);

        paint.setAntiAlias(false);
        textPaint.setAntiAlias(true);

        widthPxs = this.getWidth();
        heightPxs = this.getHeight();
    }

    @NotNull
    public Bitmap captureScreenshot() {
        final Bitmap result = Bitmap.createBitmap(widthPxs, heightPxs, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        onDraw(canvas);
        return result;
    }

    @Override
    public void setXRange(float xMin, float xMax) {
        this.graphWidth = xMax - xMin;
        this.x0 = xMin + graphWidth / 2;
        this.y0 = 0;
    }

    private void clearAllGraphs() {
        for (GraphData graph : graphs) {
            graph.clear();
        }

        while (graphViewHelper.getFunctionPlotDefs().size() > graphs.size()) {
            graphs.add(GraphData.newEmptyInstance());
        }
    }

    @Override
    public void init(@NotNull FunctionViewDef functionViewDef) {
        this.graphViewHelper = GraphViewHelper.newInstance(functionViewDef, Collections.<ArityPlotFunction>emptyList());
    }

    public void setFunctionPlotDefs(@NotNull List<ArityPlotFunction> functionPlotDefs) {

        for (ArityPlotFunction functionPlotDef : functionPlotDefs) {
            final int arity = functionPlotDef.getFunction().arity();
            if (arity != 0 && arity != 1) {
                throw new IllegalArgumentException("Function must have arity 0 or 1 for 2d plot!");
            }
        }

        this.graphViewHelper = this.graphViewHelper.copy(functionPlotDefs);
        clearAllGraphs();
        invalidate();
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDetachedFromWindow() {
        zoomController.setVisible(false);
        super.onDetachedFromWindow();
    }

    protected void onSizeChanged(int w, int h, int ow, int oh) {
        widthPxs = w;
        heightPxs = h;
        clearAllGraphs();
    }

    protected void onDraw(Canvas canvas) {
        if (graphViewHelper.getFunctionPlotDefs().size() == 0) {
            return;
        }
        if (scroller.computeScrollOffset()) {
            final float ration = getRatio();
            x0 = scroller.getCurrX() * ration;
            y0 = scroller.getCurrY() * ration;
            if (!scroller.isFinished()) {
                invalidate();
            }
        }
        drawGraph(canvas);
    }

    private float eval(Function f, float x) {
        float v = (float) f.eval(x);
        // Calculator.log("eval " + x + "; " + v); 
        if (v < -10000f) {
            return -10000f;
        }
        if (v > 10000f) {
            return 10000f;
        }
        return v;
    }

    // distance from (x,y) to the line (x1,y1) to (x2,y2), squared, multiplied by 4
    /*
    private float distance(float x1, float y1, float x2, float y2, float x, float y) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float mx = x - x1;
        float my = y - y1;
        float up = dx*my - dy*mx;
        return up*up*4/(dx*dx + dy*dy);
    }
    */

    // distance as above when x==(x1+x2)/2. 
    private float distance2(float x1, float y1, float x2, float y2, float y) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float up = dx * (y1 + y2 - y - y);
        return up * up / (dx * dx + dy * dy);
    }

    private void computeGraph(@NotNull Function function,
                              float xMin,
                              float xMax,
                              float yMin,
                              float yMax,
                              @NotNull GraphData graph) {
        if (function.arity() == 0) {
            final float v = (float) function.eval();
            graph.clear();
            graph.push(xMin, v);
            graph.push(xMax, v);
            return;
        }

        // prepare graph
        if (!graph.empty()) {
            if (xMin >= lastXMin) {
                graph.eraseBefore(xMin);
            } else {
                graph.eraseAfter(xMax);
                xMax = Math.min(xMax, graph.firstX());
                graph.swap(endGraph);
            }
        }
        if (graph.empty()) {
            graph.push(xMin, eval(function, xMin));
        }

        final float ratio = getRatio();
        final float maxStep = 15.8976f * ratio;
        final float minStep = .05f * ratio;
        float ythresh = ratio;
        ythresh = ythresh * ythresh;


        float leftX, leftY;
        float rightX = graph.topX(), rightY = graph.topY();
        int nEval = 1;
        while (true) {
            leftX = rightX;
            leftY = rightY;
            if (leftX > xMax) {
                break;
            }
            if (next.empty()) {
                float x = leftX + maxStep;
                next.push(x, eval(function, x));
                ++nEval;
            }
            rightX = next.topX();
            rightY = next.topY();
            next.pop();

            if (leftY != leftY && rightY != rightY) { // NaN
                continue;
            }

            float dx = rightX - leftX;
            float middleX = (leftX + rightX) / 2;
            float middleY = eval(function, middleX);
            ++nEval;
            boolean middleIsOutside = (middleY < leftY && middleY < rightY) || (leftY < middleY && rightY < middleY);
            if (dx < minStep) {
                // Calculator.log("minStep");
                if (middleIsOutside) {
                    graph.push(rightX, Float.NaN);
                }
                graph.push(rightX, rightY);
                continue;
            }
            if (middleIsOutside && ((leftY < yMin && rightY > yMax) || (leftY > yMax && rightY < yMin))) {
                graph.push(rightX, Float.NaN);
                graph.push(rightX, rightY);
                // Calculator.log("+-inf");
                continue;
            }

            if (!middleIsOutside) {
                /*
                float diff = leftY + rightY - middleY - middleY;
                float dy = rightY - leftY;
                float dx2 = dx*dx;
                float distance = dx2*diff*diff/(dx2+dy*dy);
                */
                // Calculator.log("" + dx + ' ' + leftY + ' ' + middleY + ' ' + rightY + ' ' + distance + ' ' + ythresh);
                if (distance2(leftX, leftY, rightX, rightY, middleY) < ythresh) {
                    graph.push(rightX, rightY);
                    continue;
                }
            }
            next.push(rightX, rightY);
            next.push(middleX, middleY);
            rightX = leftX;
            rightY = leftY;
        }
        if (!endGraph.empty()) {
            graph.append(endGraph);
        }
        long t2 = System.currentTimeMillis();
        // Calculator.log("graph points " + graph.size + " evals " + nEval + " time " + (t2-t1));

        next.clear();
        endGraph.clear();
    }

    private static void graphToPath(@NotNull GraphData graph, @NotNull Path path) {

        final int size = graph.getSize();
        final float[] xs = graph.getXs();
        final float[] ys = graph.getYs();

        path.rewind();

        boolean newCurve = true;

        for (int i = 0; i < size; i++) {

            final float y = ys[i];
            final float x = xs[i];

            if (Float.isNaN(y)) {
                newCurve = true;
            } else {
                if (newCurve) {
                    path.moveTo(x, y);
                    newCurve = false;
                } else {
                    path.lineTo(x, y);
                }
            }
        }
    }

    private static float getStep(float width) {
        float f = 1;
        while (width / f > TICKS_COUNT) {
            f *= 10;
        }
        
        while (width / f < TICKS_COUNT / 10) {
            f /= 10;
        }

        final float r = width / f;
        if (r < TICKS_COUNT / 5) {
            return f / 5;
        } else if (r < TICKS_COUNT / 2) {
            return f / 2;
        } else {
            return f;
        }
    }

    private void drawGraph(@NotNull Canvas canvas) {
        final float graphHeight = getGraphHeight();

        final float xMin = getXMin();
        final float xMax = getXMax(xMin);

        final float yMin = getYMin(graphHeight);
        final float yMax = getYMax(graphHeight, yMin);

        if (yMin < lastYMin || yMax > lastYMax) {
            float halfGraphHeight = graphHeight / 2;
            lastYMin = yMin - halfGraphHeight;
            lastYMax = yMax + halfGraphHeight;
            clearAllGraphs();
        }

        // set background
        canvas.drawColor(graphViewHelper.getFunctionViewDef().getBackgroundColor());

        // prepare paint
        paint.setStrokeWidth(0);
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);

        final float ratio = getRatio();

        float x0px = -xMin / ratio;
        if (x0px < 25) {
            x0px = 25;
        } else if (x0px > widthPxs - 3) {
            x0px = widthPxs - 3;
        }

        float y0px = yMax / ratio;
        if (y0px < 3) {
            y0px = 3;
        } else if (y0px > heightPxs - 15) {
            y0px = heightPxs - 15;
        }


        {
            // GRID

            paint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
            paint.setColor(graphViewHelper.getFunctionViewDef().getGridColor());

            textPaint.setColor(graphViewHelper.getFunctionViewDef().getAxisLabelsColor());
            textPaint.setTextSize(12);
            textPaint.setTextAlign(Paint.Align.CENTER);

            final float step = getStep(graphWidth);
            final int tickDigits = countTickDigits(step);

            // round xMin and init first tick
            float tick = ((int) (xMin / step)) * step;

            final float y2 = y0px + TICK_SIZE_PXS;

            final float stepPxs = step / ratio;

            for (float xPxs = (tick - xMin) / ratio; xPxs <= widthPxs; xPxs += stepPxs, tick += step) {
                // draw grid line
                canvas.drawLine(xPxs, 0, xPxs, heightPxs, paint);

                final CharSequence tickLabel = formatTick(tick, tickDigits);

                // draw tick label
                canvas.drawText(tickLabel, 0, tickLabel.length(), xPxs, y2 + 10, textPaint);
            }

            final float x1 = x0px - TICK_SIZE_PXS;
            tick = ((int) (yMin / step)) * step;
            textPaint.setTextAlign(Paint.Align.RIGHT);
            for (float y = heightPxs - (tick - yMin) / ratio; y >= 0; y -= stepPxs, tick += step) {
                canvas.drawLine(0, y, widthPxs, y, paint);

                final CharSequence tickLabel = formatTick(tick, tickDigits);

                // draw tick label
                canvas.drawText(tickLabel, 0, tickLabel.length(), x1, y + 4, textPaint);
            }

            paint.setPathEffect(null);
        }

        {
            // AXIS

            paint.setColor(graphViewHelper.getFunctionViewDef().getAxisColor());
            canvas.drawLine(x0px, 0, x0px, heightPxs, paint);
            canvas.drawLine(0, y0px, widthPxs, y0px, paint);
        }


        matrix.reset();
        matrix.preTranslate(-this.x0, -this.y0);
        matrix.postScale(1/ratio, -1/ratio);
        matrix.postTranslate(widthPxs / 2, heightPxs / 2);

        paint.setAntiAlias(false);

        {
            //GRAPH

            final List<ArityPlotFunction> functionPlotDefs = graphViewHelper.getFunctionPlotDefs();

            // create path once
            final Path path = new Path();

            for (int i = 0; i < functionPlotDefs.size(); i++) {
                final ArityPlotFunction fpd = functionPlotDefs.get(i);
                computeGraph(fpd.getFunction(), xMin, xMax, lastYMin, lastYMax, graphs.get(i));

                graphToPath(graphs.get(i), path);

                path.transform(matrix);

                AbstractCalculatorPlotFragment.applyToPaint(fpd.getLineDef(), paint);

                canvas.drawPath(path, paint);
            }
        }


        lastXMin = xMin;
    }

    /*
    **********************************************************************
    *
    *                           TICK FORMAT
    *
    **********************************************************************
    */

    @NotNull
    public static CharSequence formatTick(final float tickValue, final int tickDigits) {
        String result = "0";

        if (tickValue != 0f) {
            if (tickDigits < MAX_TICK_DIGITS) {
                result = String.format(TICK_FORMATS[tickDigits], tickValue);
            } else {
                // x.xxE-10 notation
                result = tickFormat.format(tickValue);
            }
        }

        return result;
    }

    public static int countTickDigits(float step) {
        if ( step >= 1 ) {
            return 0;
        } else {
            int tickDigits = 0;
            while ( step < 1 ) {
                step *= 10;
                tickDigits++;
            }
            return tickDigits;
        }
    }

    /*
    **********************************************************************
    *
    *                           BOUNDARIES
    *
    **********************************************************************
    */

    // X

    public float getXMin() {
        return x0 - graphWidth / 2;
    }

    private float getXMax(float minX) {
        return minX + graphWidth;
    }

    public float getXMax() {
        return getXMax(getXMin());
    }

    // Y

    @Override
    public float getYMin() {
        return getYMin(getGraphHeight());
    }


    public float getYMin(float graphHeight) {
        return y0 - graphHeight / 2;
    }

    @Override
    public float getYMax() {
        final float graphHeight = getGraphHeight();
        return getYMax(graphHeight, getYMin(graphHeight));
    }

    public float getYMax(float graphHeight, float yMin) {
        return yMin + graphHeight;
    }

    private float getGraphHeight() {
        return graphWidth * getAspectRatio();
    }

    private float getRatio() {
        if (widthPxs != 0) {
            return  graphWidth / widthPxs;
        } else {
            return 0;
        }
    }

    private int getAspectRatio() {
        if (widthPxs != 0) {
            return heightPxs / widthPxs;
        } else {
            return 0;
        }
    }

    /*
    **********************************************************************
    *
    *                           ZOOM
    *
    **********************************************************************
    */

    private boolean canZoomIn() {
        return true;
    }

    private boolean canZoomOut() {
        return true;
    }

    public void onVisibilityChanged(boolean visible) {
    }

    public void onZoom(boolean zoomIn) {
        if (zoomIn) {
            if (canZoomIn()) {
                graphWidth /= 2;
                invalidateGraphs();
            }
        } else {
            if (canZoomOut()) {
                graphWidth *= 2;
                invalidateGraphs();
            }
        }
        zoomController.setZoomInEnabled(canZoomIn());
        zoomController.setZoomOutEnabled(canZoomOut());
    }

    /*
    **********************************************************************
    *
    *                           TOUCH
    *
    **********************************************************************
    */

    @Override
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        boolean handled = touchHandler.handleTouchEvent(event);
        if (!handled) {
            handled = super.onTouchEvent(event);
        }
        return handled;
    }

    public void onTouchDown(float x, float y) {
        zoomController.setVisible(true);
        if (!scroller.isFinished()) {
            scroller.abortAnimation();
        }
        lastTouchX = x;
        lastTouchY = y;
    }

    public void onTouchMove(float x, float y) {
        float deltaX = x - lastTouchX;
        float deltaY = y - lastTouchY;
        if (deltaX < -1 || deltaX > 1 || deltaY < -1 || deltaY > 1) {
            scroll(-deltaX, deltaY);
            lastTouchX = x;
            lastTouchY = y;
            invalidate();
        }
    }

    public void onTouchUp(float x, float y) {
        final float ratio = getRatio();

        float sx = -touchHandler.getXVelocity();
        float sy = touchHandler.getYVelocity();

        final float asx = Math.abs(sx);
        final float asy = Math.abs(sy);
        if (asx < asy / 3) {
            sx = 0;
        } else if (asy < asx / 3) {
            sy = 0;
        }
        scroller.fling(Math.round(x0 / ratio), Math.round(y0 / ratio), Math.round(sx), Math.round(sy), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    public void onTouchZoomDown(float x1, float y1, float x2, float y2) {
        zoomTracker.start(graphWidth, x1, y1, x2, y2);
    }

    public void onTouchZoomMove(float x1, float y1, float x2, float y2) {
        if (!zoomTracker.update(x1, y1, x2, y2)) {
            return;
        }
        float targetGwidth = zoomTracker.value;
        if (targetGwidth > .25f && targetGwidth < 200) {
            graphWidth = targetGwidth;
        }
        // scroll(-zoomTracker.moveX, zoomTracker.moveY);
        invalidateGraphs();
        // Calculator.log("zoom redraw");
    }

    private void invalidateGraphs() {
        clearAllGraphs();
        lastYMin = lastYMax = 0;
        invalidate();
    }

    private void scroll(float deltaX, float deltaY) {
        final float scale = graphWidth / widthPxs;
        float dx = deltaX * scale;
        float dy = deltaY * scale;
        final float adx = Math.abs(dx);
        final float ady = Math.abs(dy);
        if (adx < ady / 3) {
            dx = 0;
        } else if (ady < adx / 3) {
            dy = 0;
        }
        x0 += dx;
        y0 += dy;
    }
}
