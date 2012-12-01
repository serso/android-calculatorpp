package org.solovyev.android.calculator.onscreen;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.*;

/**
 * User: serso
 * Date: 11/21/12
 * Time: 9:03 PM
 */
public class CalculatorOnscreenView {
    /*
    **********************************************************************
    *
    *                           CONSTANTS
    *
    **********************************************************************
    */

    private static final String TAG = CalculatorOnscreenView.class.getSimpleName();

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @NotNull
    private View root;

    @NotNull
    private View content;

	@NotNull
	private View header;

	@NotNull
	private AndroidCalculatorEditorView editorView;

	@NotNull
	private AndroidCalculatorDisplayView displayView;

	@NotNull
    private Context context;

    private int width;

    private int height;

    private int unfoldedHeight;

    @NotNull
    private String cursorColor;

    @Nullable
    private OnscreenViewListener viewListener;

    /*
    **********************************************************************
    *
    *                           STATES
    *
    **********************************************************************
    */

    private boolean minimized = false;

    private boolean attached = false;

    private boolean folded = false;

    private boolean initialized = false;

    private boolean hidden = true;


    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

    private CalculatorOnscreenView() {
    }

    public static CalculatorOnscreenView newInstance(@NotNull Context context,
                                                     @NotNull CalculatorOnscreenViewDef def,
                                                     @NotNull String cursorColor,
                                                     @Nullable OnscreenViewListener viewListener) {
        final CalculatorOnscreenView result = new CalculatorOnscreenView();

        result.root = View.inflate(context, R.layout.onscreen_layout, null);
        result.context = context;
        result.width = def.getWidth();
        result.height = def.getHeight();
        result.cursorColor = cursorColor;
        result.viewListener = viewListener;

        return result;
    }

    /*
    **********************************************************************
    *
    *                           METHODS
    *
    **********************************************************************
    */

    public void updateDisplayState(@NotNull CalculatorDisplayViewState displayState) {
        checkInit();
		displayView.setState(displayState);
    }

    public void updateEditorState(@NotNull CalculatorEditorViewState editorState) {
        checkInit();
		editorView.setState(editorState);
    }

    private void setHeight(int height) {
        checkInit();

        this.height = height;

        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) root.getLayoutParams();

        params.height = height;

        getWindowManager().updateViewLayout(root, params);
    }

    /*
    **********************************************************************
    *
    *                           LIFECYCLE
    *
    **********************************************************************
    */

    private void init() {

        if (!initialized) {
            for (final CalculatorButton widgetButton : CalculatorButton.values()) {
                final View button = root.findViewById(widgetButton.getButtonId());
                if (button != null) {
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
							widgetButton.onClick(context);
							if ( widgetButton == CalculatorButton.app ) {
								minimize();
							}
                        }
                    });
                    button.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							widgetButton.onLongClick(context);
							return true;
						}
					});
                }
            }

            final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

			header = root.findViewById(R.id.onscreen_header);
            content = root.findViewById(R.id.onscreen_content);

			displayView = (AndroidCalculatorDisplayView) root.findViewById(R.id.calculator_display);
			displayView.init(this.context, false);

			editorView = (AndroidCalculatorEditorView) root.findViewById(R.id.calculator_editor);
			editorView.init(this.context);

            final View onscreenFoldButton = root.findViewById(R.id.onscreen_fold_button);
            onscreenFoldButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (folded) {
                        unfold();
                    } else {
                        fold();
                    }
                }
            });

            final View onscreenHideButton = root.findViewById(R.id.onscreen_minimize_button);
            onscreenHideButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    minimize();
                }
            });

            root.findViewById(R.id.onscreen_close_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hide();
                }
            });

            final ImageView onscreenTitleImageView = (ImageView) root.findViewById(R.id.onscreen_title);
            onscreenTitleImageView.setOnTouchListener(new WindowDragTouchListener(wm, root, context.getResources().getDisplayMetrics()));

            initialized = true;
        }

    }

    private void checkInit() {
        if (!initialized) {
            throw new IllegalStateException("init() must be called!");
        }
    }

    public void show() {
        if (hidden) {
            init();
            attach();

            hidden = false;
        }
    }

    public void attach() {
        checkInit();

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (!attached) {
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    width,
                    height,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.TOP | Gravity.LEFT;

            wm.addView(root, params);
            attached = true;
        }
    }

    private void fold() {
        if (!folded) {
            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) root.getLayoutParams();
            unfoldedHeight = params.height;
            int newHeight = header.getHeight();
            content.setVisibility(View.GONE);
            setHeight(newHeight);
            folded = true;
        }
    }

    private void unfold() {
        if (folded) {
            content.setVisibility(View.VISIBLE);
            setHeight(unfoldedHeight);
            folded = false;
        }
    }

    public void detach() {
        checkInit();

        if (attached) {
            getWindowManager().removeView(root);
            attached = false;
        }
    }

    public void minimize() {
        checkInit();
        if (!minimized) {
            detach();

            if (viewListener != null) {
                viewListener.onViewMinimized();
            }

            minimized = true;
        }
    }

    public void hide() {
        checkInit();

        if (!hidden) {

            detach();

            if (viewListener != null) {
                viewListener.onViewHidden();
            }

            hidden = true;
        }
    }

    @NotNull
    private WindowManager getWindowManager() {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    }

    @NotNull
    public CalculatorOnscreenViewDef getCalculatorOnscreenViewDef() {
        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) root.getLayoutParams();
        return CalculatorOnscreenViewDef.newInstance(width, height, params.x, params.y);
    }

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

    private static class WindowDragTouchListener implements View.OnTouchListener {

        /*
        **********************************************************************
        *
        *                           CONSTANTS
        *
        **********************************************************************
        */

        private static final float DIST_EPS = 0f;
        private static final float DIST_MAX = 100000f;
        private static final long TIME_EPS = 0L;

        /*
        **********************************************************************
        *
        *                           FIELDS
        *
        **********************************************************************
        */

        @NotNull
        private final WindowManager wm;

        private float x0;

        private float y0;

        private long time = 0;

        @NotNull
        private final View view;

        private final int displayWidth;

        private final int displayHeight;

        /*
        **********************************************************************
        *
        *                           CONSTRUCTORS
        *
        **********************************************************************
        */

        public WindowDragTouchListener(@NotNull WindowManager wm,
                                       @NotNull View view,
                                       @NotNull DisplayMetrics displayMetrics) {
            this.wm = wm;
            this.view = view;
            this.displayWidth = displayMetrics.widthPixels;
            this.displayHeight = (int) (displayMetrics.heightPixels - 25 * displayMetrics.density);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            //Log.d(TAG, "Action: " + event.getAction());

            final float x1 = event.getRawX();
            final float y1 = event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "0:" + toString(x0, y0) + ", 1: " + toString(x1, y1));
                    x0 = x1;
                    y0 = y1;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    final long currentTime = System.currentTimeMillis();

                    if ( currentTime - time >= TIME_EPS ) {
                        time = currentTime;
                        /*for (int i = 0; i < event.getHistorySize(); i++) {
                            final float xi = event.getHistoricalX(i);
                            final float yi = event.getHistoricalY(i);
                            processMove(xi, yi);
                        }*/
                        processMove(x1, y1, event.getPointerCount());
                    }
                    return true;
            }

            return false;
        }

        private void processMove(float x1, float y1, int pointerCount) {
            final float Δx = x1 - x0;
            final float Δy = y1 - y0;

            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
            Log.d(TAG, "0:" + toString(x0, y0) + ", 1: " + toString(x1, y1) + ", Δ: " + toString(Δx, Δy) + ", params: " + toString(params.x, params.y));

            boolean xInBounds = isDistanceInBounds(Δx);
            boolean yInBounds = isDistanceInBounds(Δy);
            if (xInBounds || yInBounds) {

                if (pointerCount == 1) {
                    params.x = (int) (params.x + Δx);
                    params.y = (int) (params.y + Δy);
                }

                params.x = (int) (params.x - params.width * 0.5f);
                params.y = (int) (params.y - params.height * 0.5f);

                // keep window inside edges
                params.x = Math.min(Math.max(params.x, 0), displayWidth - params.width);
                params.y = Math.min(Math.max(params.y, 0), displayHeight - params.height);

                wm.updateViewLayout(view, params);

                x0 = x1;
                y0 = y1;
            }
        }

        private boolean isDistanceInBounds(float δx) {
            δx = Math.abs(δx);
            return δx >= DIST_EPS && δx < DIST_MAX;
        }

        @NotNull
        private static String toString(float x, float y) {
            return "(" + formatFloat(x) + ", " + formatFloat(y) + ")";
        }

        private static String formatFloat(float value) {
            if (value >= 0) {
                return "+" + String.format("%.2f", value);
            } else {
                return String.format("%.2f", value);
            }
        }
    }
}
