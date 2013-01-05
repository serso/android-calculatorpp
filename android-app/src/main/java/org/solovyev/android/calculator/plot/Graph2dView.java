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
import org.solovyev.android.AndroidUtils2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph2dView extends View implements GraphView {

    private int width, height;
    private Matrix matrix = new Matrix();
    private Paint paint = new Paint(), textPaint = new Paint(), fillPaint = new Paint();

    @NotNull
    private GraphViewHelper graphViewHelper = GraphViewHelper.newDefaultInstance();

    private final GraphData next = GraphData.newEmptyInstance();

    private final GraphData endGraph = GraphData.newEmptyInstance();

    @NotNull
    private List<GraphData> graphs = new ArrayList<GraphData>(graphViewHelper.getFunctionPlotDefs().size());

    private float gwidth = 8;
    private float currentX, currentY;
    private float lastMinX;
    private Scroller scroller;
    private float boundMinY, boundMaxY;
    protected ZoomButtonsController zoomController = new ZoomButtonsController(this);
    private ZoomTracker zoomTracker = new ZoomTracker();
    private TouchHandler touchHandler;
    private float lastTouchX, lastTouchY;

    private static final int
            COL_ZOOM = 0x40ffffff,
            COL_ZOOM_TEXT1 = 0xd0ffffff,
            COL_ZOOM_TEXT2 = 0x30ffffff;

    public Graph2dView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Graph2dView(Context context) {
        super(context);
        touchHandler = new TouchHandler(this);
        init(context);
    }

    private void init(Context context) {
        zoomController.setOnZoomListener(this);
        scroller = new Scroller(context);
        paint.setAntiAlias(false);
        textPaint.setAntiAlias(true);
    }

    public String captureScreenshot() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        onDraw(canvas);
        return AndroidUtils2.saveBitmap(bitmap, GraphView.SCREENSHOT_DIR, "calculator");
    }

    private void clearAllGraphs() {
        for (GraphData graph : graphs) {
           graph.clear();
        }

        while ( graphViewHelper.getFunctionPlotDefs().size() > graphs.size() ) {
            graphs.add(GraphData.newEmptyInstance());
        }
    }

    @Override
    public void init(@NotNull FunctionViewDef functionViewDef) {
        this.graphViewHelper = GraphViewHelper.newInstance(functionViewDef, Collections.<FunctionPlotDef>emptyList());
    }

    public void setFunctionPlotDefs(@NotNull List<FunctionPlotDef> functionPlotDefs) {

        for (FunctionPlotDef functionPlotDef: functionPlotDefs) {
            final int arity = functionPlotDef.getFunction().arity();
            if (arity != 0 && arity != 1) {
                throw new IllegalArgumentException("Function must have arity 0 or 1 for 2d plot!");
            }
        }

        this.graphViewHelper = this.graphViewHelper.copy(functionPlotDefs);
        clearAllGraphs();
        invalidate();
    }

    public void onVisibilityChanged(boolean visible) {
    }

    public void onZoom(boolean zoomIn) {
        if (zoomIn) {
            if (canZoomIn()) {
                gwidth /= 2;
                invalidateGraphs();
            }
        } else {
            if (canZoomOut()) {
                gwidth *= 2;
                invalidateGraphs();
            }
        }
        zoomController.setZoomInEnabled(canZoomIn());
        zoomController.setZoomOutEnabled(canZoomOut());
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
        width = w;
        height = h;
        clearAllGraphs();
        // points = new float[w+w];
    }

    protected void onDraw(Canvas canvas) {
        if (graphViewHelper.getFunctionPlotDefs().size() == 0) {
            return;
        }
        if (scroller.computeScrollOffset()) {
            final float scale = gwidth / width;
            currentX = scroller.getCurrX() * scale;
            currentY = scroller.getCurrY() * scale;
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
                              float minX,
                              float maxX,
                              float minY,
                              float maxY,
                              @NotNull GraphData graph) {
        if (function.arity() == 0) {
            float v = (float) function.eval();
            if (v < -10000f) {
                v = -10000f;
            }
            if (v > 10000f) {
                v = 10000f;
            }
            graph.clear();
            graph.push(minX, v);
            graph.push(maxX, v);
            return;
        }

        final float scale = width / gwidth;
        final float maxStep = 15.8976f / scale;
        final float minStep = .05f / scale;
        float ythresh = 1 / scale;
        ythresh = ythresh * ythresh;
        // next.clear();
        // endGraph.clear();
        if (!graph.empty()) {
            // Calculator.log("last " + lastMinX + " min " + minX);
            if (minX >= lastMinX) {
                graph.eraseBefore(minX);
            } else {
                graph.eraseAfter(maxX);
                maxX = Math.min(maxX, graph.firstX());
                graph.swap(endGraph);
            }
        }
        if (graph.empty()) {
            graph.push(minX, eval(function, minX));
        }
        float leftX, leftY;
        float rightX = graph.topX(), rightY = graph.topY();
        int nEval = 1;
        while (true) {
            leftX = rightX;
            leftY = rightY;
            if (leftX > maxX) {
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
            if (middleIsOutside && ((leftY < minY && rightY > maxY) || (leftY > maxY && rightY < minY))) {
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

            if (y != y) {
                newCurve = true;
            } else { // !NaN
                if (newCurve) {
                    path.moveTo(x, y);
                    newCurve = false;
                } else {
                    path.lineTo(x, y);
                }
            }
        }
    }

    private static final float NTICKS = 15;

    private static float stepFactor(float w) {
        float f = 1;
        while (w / f > NTICKS) {
            f *= 10;
        }
        while (w / f < NTICKS / 10) {
            f /= 10;
        }
        float r = w / f;
        if (r < NTICKS / 5) {
            return f / 5;
        } else if (r < NTICKS / 2) {
            return f / 2;
        } else {
            return f;
        }
    }

    private static StringBuilder b = new StringBuilder();
    private static char[] buf = new char[20];

    private static StringBuilder format(float fv) {
        int pos = 0;
        boolean addDot = false;
        int v = Math.round(fv * 100);
        boolean isNeg = v < 0;
        v = isNeg ? -v : v;
        for (int i = 0; i < 2; ++i) {
            int digit = v % 10;
            v /= 10;
            if (digit != 0 || addDot) {
                buf[pos++] = (char) ('0' + digit);
                addDot = true;
            }
        }
        if (addDot) {
            buf[pos++] = '.';
        }
        if (v == 0) {
            buf[pos++] = '0';
        }
        while (v != 0) {
            buf[pos++] = (char) ('0' + (v % 10));
            v /= 10;
        }
        if (isNeg) {
            buf[pos++] = '-';
        }
        b.setLength(0);
        b.append(buf, 0, pos);
        b.reverse();
        return b;
    }

    private void drawGraph(Canvas canvas) {
        long t1 = System.currentTimeMillis();
        float minX = getXMin();
        float maxX = getXMax(minX);
        float ywidth = gwidth * height / width;
        float minY = currentY - ywidth / 2;
        float maxY = minY + ywidth;
        if (minY < boundMinY || maxY > boundMaxY) {
            float halfw = ywidth / 2;
            boundMinY = minY - halfw;
            boundMaxY = maxY + halfw;
            clearAllGraphs();
        }

        canvas.drawColor(graphViewHelper.getFunctionViewDef().getBackgroundColor());

        paint.setStrokeWidth(0);
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);

        final float h2 = height / 2f;
        final float scale = width / gwidth;

        float x0 = -minX * scale;
        boolean drawYAxis = true;
        if (x0 < 25) {
            x0 = 25;
            // drawYAxis = false;
        } else if (x0 > width - 3) {
            x0 = width - 3;
            // drawYAxis = false;
        }
        float y0 = maxY * scale;
        if (y0 < 3) {
            y0 = 3;
        } else if (y0 > height - 15) {
            y0 = height - 15;
        }

        final float tickSize = 3;
        final float y2 = y0 + tickSize;


        {
            // GRID

            paint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
            paint.setColor(graphViewHelper.getFunctionViewDef().getGridColor());

            float step = stepFactor(gwidth);
            // Calculator.log("width " + gwidth + " step " + step);
            float v = ((int) (minX / step)) * step;
            textPaint.setColor(graphViewHelper.getFunctionViewDef().getAxisLabelsColor());
            textPaint.setTextSize(12);
            textPaint.setTextAlign(Paint.Align.CENTER);
            float stepScale = step * scale;
            for (float x = (v - minX) * scale; x <= width; x += stepScale, v += step) {
                canvas.drawLine(x, 0, x, height, paint);
                if (!(-.001f < v && v < .001f)) {
                    StringBuilder b = format(v);
                    canvas.drawText(b, 0, b.length(), x, y2 + 10, textPaint);
                }
            }

            final float x1 = x0 - tickSize;
            v = ((int) (minY / step)) * step;
            textPaint.setTextAlign(Paint.Align.RIGHT);
            for (float y = height - (v - minY) * scale; y >= 0; y -= stepScale, v += step) {
                canvas.drawLine(0, y, width, y, paint);
                if (!(-.001f < v && v < .001f)) {
                    StringBuilder b = format(v);
                    canvas.drawText(b, 0, b.length(), x1, y + 4, textPaint);
                }
            }

            paint.setPathEffect(null);
        }

        // AXIS

        paint.setColor(graphViewHelper.getFunctionViewDef().getAxisColor());
        if (drawYAxis) {
            canvas.drawLine(x0, 0, x0, height, paint);
        }
        canvas.drawLine(0, y0, width, y0, paint);

        matrix.reset();
        matrix.preTranslate(-currentX, -currentY);
        matrix.postScale(scale, -scale);
        matrix.postTranslate(width / 2, height / 2);

        paint.setAntiAlias(false);

        final List<FunctionPlotDef> functionPlotDefs = graphViewHelper.getFunctionPlotDefs();

        // create path once
        final Path path = new Path();

        for (int i = 0; i < functionPlotDefs.size(); i++) {
            final FunctionPlotDef fpd = functionPlotDefs.get(i);
            computeGraph(fpd.getFunction(), minX, maxX, boundMinY, boundMaxY, graphs.get(i));

            graphToPath(graphs.get(i), path);

            path.transform(matrix);

            fpd.getLineDef().applyToPaint(paint);

            canvas.drawPath(path, paint);
        }
        lastMinX = minX;
    }

    private float getXMax(float minX) {
        return minX + gwidth;
    }

    private float getXMax() {
        return getXMax(getXMin());
    }

    private float getXMin() {
        return currentX - gwidth / 2;
    }

    private boolean canZoomIn() {
        return gwidth > 1f;
    }

    private boolean canZoomOut() {
        return gwidth < 50;
    }

    private void invalidateGraphs() {
        clearAllGraphs();
        boundMinY = boundMaxY = 0;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchHandler != null ? touchHandler.onTouchEvent(event) : super.onTouchEvent(event);
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
        final float scale = width / gwidth;
        float sx = -touchHandler.velocityTracker.getXVelocity();
        float sy = touchHandler.velocityTracker.getYVelocity();
        final float asx = Math.abs(sx);
        final float asy = Math.abs(sy);
        if (asx < asy / 3) {
            sx = 0;
        } else if (asy < asx / 3) {
            sy = 0;
        }
        scroller.fling(Math.round(currentX * scale),
                Math.round(currentY * scale),
                Math.round(sx), Math.round(sy), -10000, 10000, -10000, 10000);
        invalidate();
    }

    public void onTouchZoomDown(float x1, float y1, float x2, float y2) {
        zoomTracker.start(gwidth, x1, y1, x2, y2);
    }

    public void onTouchZoomMove(float x1, float y1, float x2, float y2) {
        if (!zoomTracker.update(x1, y1, x2, y2)) {
            return;
        }
        float targetGwidth = zoomTracker.value;
        if (targetGwidth > .25f && targetGwidth < 200) {
            gwidth = targetGwidth;
        }
        // scroll(-zoomTracker.moveX, zoomTracker.moveY);
        invalidateGraphs();
        // Calculator.log("zoom redraw");
    }

    private void scroll(float deltaX, float deltaY) {
        final float scale = gwidth / width;
        float dx = deltaX * scale;
        float dy = deltaY * scale;
        final float adx = Math.abs(dx);
        final float ady = Math.abs(dy);
        if (adx < ady / 3) {
            dx = 0;
        } else if (ady < adx / 3) {
            dy = 0;
        }
        currentX += dx;
        currentY += dy;
    }
}
