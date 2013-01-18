package org.solovyev.android.calculator.plot;


import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import android.widget.ZoomButtonsController;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.math.Point2d;

import java.text.DecimalFormat;
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
	private static final int NO_TOUCH = -1;

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

	@NotNull
    private final Matrix matrix = new Matrix();

    // paints

    @NotNull
    private final Paint paint = new Paint();

    @NotNull
    private final Paint textPaint = new Paint();

    @NotNull
    private GraphViewHelper graphViewHelper = GraphViewHelper.newDefaultInstance();
    @NotNull
    private final GraphsData graphsData = new GraphsData(this);

    private float lastTouchXPxs = NO_TOUCH;
	private float lastTouchYPxs = NO_TOUCH;

    @NotNull
    private TouchHandler touchHandler;

    @NotNull
    protected ZoomButtonsController zoomController = new ZoomButtonsController(this);

    @NotNull
    private ZoomTracker zoomTracker = new ZoomTracker();

    @NotNull
    private Scroller scroller;

    @NotNull
    private final Graph2dDimensions dimensions = new Graph2dDimensions(this);

    @NotNull
    private final GraphCalculator graphCalculator = new GraphCalculatorImpl();

    private boolean mDrawn = false;

    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

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

        dimensions.setViewDimensions(this);
    }

    @Override
    public void init(@NotNull PlotViewDef plotViewDef) {
        this.graphViewHelper = GraphViewHelper.newInstance(plotViewDef, Collections.<PlotFunction>emptyList());
    }

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

    public void setPlotFunctions(@NotNull List<PlotFunction> plotFunctions) {

        for (PlotFunction plotFunction : plotFunctions) {
            final int arity = plotFunction.getXyFunction().getArity();
            if (arity != 0 && arity != 1) {
                throw new IllegalArgumentException("Function must have arity 0 or 1 for 2d plot!");
            }
        }

        this.graphViewHelper = this.graphViewHelper.copy(plotFunctions);
        invalidateGraphs();
    }

    @NotNull
    @Override
    public List<PlotFunction> getPlotFunctions() {
        return this.graphViewHelper.getPlotFunctions();
    }

    @NotNull
    public Bitmap captureScreenshot() {
        final Bitmap result = Bitmap.createBitmap(dimensions.getVWidthPxs(), dimensions.getVHeightPxs(), Bitmap.Config.RGB_565);
        onDraw(new Canvas(result));
        return result;
    }

    @Override
    public void invalidateGraphs() {
        graphsData.clear();

        if (mDrawn) {
            mDrawn = false;
            invalidate();
        }
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
        dimensions.setViewDimensions(w, h);
    }

    protected void onDraw(@NotNull Canvas canvas) {
        if (!graphViewHelper.getPlotFunctions().isEmpty()) {

            if (scroller.computeScrollOffset()) {
                final float ratio = dimensions.getGraphToViewRatio();
                dimensions.setXY(scroller.getCurrX() * ratio, scroller.getCurrY() * ratio);
                if (!scroller.isFinished()) {
                    invalidate();
                }
            }

            drawGraph(canvas);
        }
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

    private void drawGraph(@NotNull Canvas canvas) {
        mDrawn = true;

        final float graphHeight = dimensions.getGraphHeight();

        final float xMin = dimensions.getXMin();
        final float xMax = dimensions.getXMax(xMin);

        final float yMin = dimensions.getYMin(graphHeight);
        final float yMax = dimensions.getYMax(graphHeight, yMin);
        final float widthPxs = dimensions.getVWidthPxs();
        final float heightPxs = dimensions.getVHeightPxs();

        graphsData.checkBoundaries(graphHeight, yMin, yMax);

        final int tickDigits = drawGridAndAxis(canvas);

		{
			// TOUCH POSITION

			if (lastTouchXPxs != NO_TOUCH && lastTouchYPxs != NO_TOUCH) {

				paint.setColor(graphViewHelper.getPlotViewDef().getGridColor());
				paint.setAlpha(100);

				canvas.drawLine(lastTouchXPxs, 0, lastTouchXPxs, heightPxs, paint);
				canvas.drawLine(0, lastTouchYPxs, widthPxs, lastTouchYPxs, paint);

				final Point2d lastTouch = dimensions.toGraphCoordinates(lastTouchXPxs, lastTouchYPxs);
				final String touchLabel = "[" + formatTick(lastTouch.getX(), tickDigits + 1) + ", " + formatTick(lastTouch.getY(), tickDigits + 1) + "]";
				canvas.drawText(touchLabel, 0, touchLabel.length(), lastTouchXPxs - 40, lastTouchYPxs - 40, textPaint);

				// restore alpha
				paint.setAlpha(255);
			}
		}

        final float ratio = dimensions.getGraphToViewRatio();

        matrix.reset();
        matrix.preTranslate(-dimensions.getX0(), -dimensions.getY0());
        matrix.postScale(1/ratio, -1/ratio);
        matrix.postTranslate(widthPxs / 2, heightPxs / 2);

        paint.setAntiAlias(false);

        {
            //GRAPH

            final List<PlotFunction> plotFunctions = graphViewHelper.getPlotFunctions();

            // create path once
            final Path path = new Path();

            for (int i = 0; i < plotFunctions.size(); i++) {
                final PlotFunction plotFunction = plotFunctions.get(i);
                final GraphData graph = graphsData.get(i);

                graphCalculator.computeGraph(plotFunction.getXyFunction(), xMin, xMax, graph, graphsData, dimensions);

                graphToPath(graph, path);

                path.transform(matrix);

                AbstractCalculatorPlotFragment.applyToPaint(plotFunction.getPlotLineDef(), paint);

                canvas.drawPath(path, paint);
            }
        }


        graphsData.setLastXMin(xMin);
        graphsData.setLastXMax(xMax);
    }

    private int drawGridAndAxis(@NotNull Canvas canvas) {
        final float graphHeight = dimensions.getGraphHeight();

        final float xMin = dimensions.getXMin();

        final float yMin = dimensions.getYMin(graphHeight);
        final float yMax = dimensions.getYMax(graphHeight, yMin);
        final float widthPxs = dimensions.getVWidthPxs();
        final float heightPxs = dimensions.getVHeightPxs();

        // set background
        canvas.drawColor(graphViewHelper.getPlotViewDef().getBackgroundColor());

        // prepare paint
        paint.setStrokeWidth(0);
        paint.setAntiAlias(false);
        paint.setStyle(Paint.Style.STROKE);

        final float ratio = dimensions.getGraphToViewRatio();

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


        final float tickStep = getTickStep(dimensions.getGWidth());
        final int tickDigits = countTickDigits(tickStep);

        {
            // GRID

            paint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
            paint.setColor(graphViewHelper.getPlotViewDef().getGridColor());

            textPaint.setColor(graphViewHelper.getPlotViewDef().getAxisLabelsColor());
            textPaint.setTextSize(12);
            textPaint.setTextAlign(Paint.Align.CENTER);

            // round xMin and init first tick
            float tick = ((int) (xMin / tickStep)) * tickStep;

            final float y2 = y0px + TICK_SIZE_PXS;

            final float stepPxs = tickStep / ratio;

            for (float xPxs = (tick - xMin) / ratio; xPxs <= widthPxs; xPxs += stepPxs, tick += tickStep) {
                // draw grid line
                canvas.drawLine(xPxs, 0, xPxs, heightPxs, paint);

                final CharSequence tickLabel = formatTick(tick, tickDigits);

                // draw tick label
                canvas.drawText(tickLabel, 0, tickLabel.length(), xPxs, y2 + 10, textPaint);
            }

            final float x1 = x0px - TICK_SIZE_PXS;
            tick = ((int) (yMin / tickStep)) * tickStep;
            textPaint.setTextAlign(Paint.Align.RIGHT);
            for (float y = heightPxs - (tick - yMin) / ratio; y >= 0; y -= stepPxs, tick += tickStep) {
                canvas.drawLine(0, y, widthPxs, y, paint);

                final CharSequence tickLabel = formatTick(tick, tickDigits);

                // draw tick label
                canvas.drawText(tickLabel, 0, tickLabel.length(), x1, y + 4, textPaint);
            }

            paint.setPathEffect(null);
        }

        {
            // AXIS

            paint.setColor(graphViewHelper.getPlotViewDef().getAxisColor());
            canvas.drawLine(x0px, 0, x0px, heightPxs, paint);
            canvas.drawLine(0, y0px, widthPxs, y0px, paint);
        }
        return tickDigits;
    }

    /*
    **********************************************************************
    *
    *                           TICKS
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


    private static float getTickStep(float width) {
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

    /*
    **********************************************************************
    *
    *                           BOUNDARIES
    *
    **********************************************************************
    */

    // X

    public float getXMin() {
        return dimensions.getXMin();
    }

    public float getXMax() {
        return dimensions.getXMax();
    }

    // Y

    @Override
    public float getYMin() {
        return dimensions.getYMin();
    }


    @Override
    public float getYMax() {
        return dimensions.getYMax();
    }

    @Override
    public void setXRange(float xMin, float xMax) {
        this.dimensions.setXRange(xMin, xMax);
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
                dimensions.setGWidth(dimensions.getGWidth() / 2);
            }
        } else {
            if (canZoomOut()) {
                dimensions.setGWidth(dimensions.getGWidth() * 2);
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

		lastTouchXPxs = x;
        lastTouchYPxs = y;
    }

    public void onTouchMove(float x, float y) {
        float deltaX = x - lastTouchXPxs;
        float deltaY = y - lastTouchYPxs;

        if (deltaX < -1 || deltaX > 1 || deltaY < -1 || deltaY > 1) {
            scroll(-deltaX, deltaY);
            lastTouchXPxs = x;
            lastTouchYPxs = y;
            invalidate();
        }
    }

    public void onTouchUp(float x, float y) {
        final float ratio = dimensions.getGraphToViewRatio();

		lastTouchXPxs = NO_TOUCH;
		lastTouchYPxs = NO_TOUCH;

        float sx = -touchHandler.getXVelocity();
        float sy = touchHandler.getYVelocity();

        final float asx = Math.abs(sx);
        final float asy = Math.abs(sy);
        if (asx < asy / 3) {
            sx = 0;
        } else if (asy < asx / 3) {
            sy = 0;
        }
        scroller.fling(Math.round(dimensions.getX0() / ratio), Math.round(dimensions.getY0() / ratio), Math.round(sx), Math.round(sy), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }

    public void onTouchZoomDown(float x1, float y1, float x2, float y2) {
        zoomTracker.start(dimensions.getGWidth(), x1, y1, x2, y2);
    }

    public void onTouchZoomMove(float x1, float y1, float x2, float y2) {
        if (!zoomTracker.update(x1, y1, x2, y2)) {
            return;
        }
        float targetGWidth = zoomTracker.value;
        if (targetGWidth > .25f && targetGWidth < 200) {
            dimensions.setGWidth(targetGWidth);
        }
    }

    private void scroll(float deltaX, float deltaY) {
        final float scale = dimensions.getGWidth() / dimensions.getVWidthPxs();
        float dx = deltaX * scale;
        float dy = deltaY * scale;
        final float adx = Math.abs(dx);
        final float ady = Math.abs(dy);
        if (adx < ady / 3) {
            dx = 0;
        } else if (ady < adx / 3) {
            dy = 0;
        }

        dimensions.increaseXY(dx, dy);
    }
}
