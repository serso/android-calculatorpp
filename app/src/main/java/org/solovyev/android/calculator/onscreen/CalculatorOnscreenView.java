/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.onscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import org.solovyev.android.calculator.CalculatorButton;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.DisplayView;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.EditorView;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.prefs.Preference;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CalculatorOnscreenView {
    private static final String TAG = CalculatorOnscreenView.class.getSimpleName();

    private static final Preference<CalculatorOnscreenViewState> viewStatePreference = new CalculatorOnscreenViewState.Preference("onscreen_view_state", CalculatorOnscreenViewState.createDefault());

    @Nonnull
    private View root;
    @Nonnull
    private View content;
    @Nonnull
    private View header;
    @Nonnull
    private ImageView headerTitle;
    private Drawable headerTitleDrawable;
    @Nonnull
    private EditorView editorView;
    @Nonnull
    private DisplayView displayView;
    @Nonnull
    private Context context;
    @Nonnull
    private CalculatorOnscreenViewState state = CalculatorOnscreenViewState.createDefault();
    @Nullable
    private OnscreenViewListener viewListener;
    @Nonnull
    private Keyboard keyboard;

    private boolean minimized;
    private boolean attached;
    private boolean folded;
    private boolean initialized;
    private boolean shown;


    private CalculatorOnscreenView() {
    }

    public static CalculatorOnscreenView create(@Nonnull Context context,
                                                @Nonnull CalculatorOnscreenViewState state,
                                                @Nullable OnscreenViewListener viewListener,
                                                @NonNull SharedPreferences preferences,
                                                @NonNull Keyboard keyboard) {
        final CalculatorOnscreenView view = new CalculatorOnscreenView();

        final Preferences.SimpleTheme theme = Preferences.Onscreen.theme.getPreferenceNoError(preferences);
        final Preferences.Gui.Theme appTheme = Preferences.Gui.theme.getPreferenceNoError(preferences);
        view.root = View.inflate(context, theme.getOnscreenLayout(appTheme), null);
        view.context = context;
        view.viewListener = viewListener;
        view.keyboard = keyboard;

        final CalculatorOnscreenViewState persistedState = readState(context);
        if (persistedState != null) {
            view.state = persistedState;
        } else {
            view.state = state;
        }

        return view;
    }

    public static void persistState(@Nonnull Context context, @Nonnull CalculatorOnscreenViewState state) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        viewStatePreference.putPreference(preferences, state);
    }

    @Nullable
    public static CalculatorOnscreenViewState readState(@Nonnull Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (viewStatePreference.isSet(preferences)) {
            return viewStatePreference.getPreference(preferences);
        } else {
            return null;
        }
    }

    public void updateDisplayState(@Nonnull DisplayState displayState) {
        checkInit();
        displayView.setState(displayState);
    }

    public void updateEditorState(@Nonnull EditorState editorState) {
        checkInit();
        editorView.setState(editorState);
    }

    private void setHeight(int height) {
        checkInit();

        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) root.getLayoutParams();
        params.height = height;
        getWindowManager().updateViewLayout(root, params);
    }

    private void init() {
        if (initialized) {
            return;
        }

        for (final CalculatorButton widgetButton : CalculatorButton.values()) {
            final View button = root.findViewById(widgetButton.getButtonId());
            if (button == null) {
                continue;
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (keyboard.buttonPressed(widgetButton.action)) {
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    }
                    if (widgetButton == CalculatorButton.app) {
                        minimize();
                    }
                }
            });
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (keyboard.buttonPressed(widgetButton.actionLong)) {
                        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    }
                    return true;
                }
            });
        }

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        header = root.findViewById(R.id.onscreen_header);
        headerTitle = (ImageView) header.findViewById(R.id.onscreen_title);
        headerTitleDrawable = headerTitle.getDrawable();
        headerTitle.setImageDrawable(null);
        content = root.findViewById(R.id.onscreen_content);

        displayView = (DisplayView) root.findViewById(R.id.calculator_display);

        editorView = (EditorView) root.findViewById(R.id.calculator_editor);

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

        headerTitle.setOnTouchListener(new WindowDragTouchListener(wm, root));

        initialized = true;

    }

    private void checkInit() {
        if (!initialized) {
            throw new IllegalStateException("init() must be called!");
        }
    }

    public void show() {
        if (shown) {
            return;
        }
        init();
        attach();

        shown = true;
    }

    public void attach() {
        checkInit();

        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (!attached) {
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    state.getWidth(),
                    state.getHeight(),
                    state.getX(),
                    state.getY(),
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
            headerTitle.setImageDrawable(headerTitleDrawable);
            final Resources r = header.getResources();
            final int newHeight = header.getHeight() + 2 * r.getDimensionPixelSize(R.dimen.cpp_onscreen_main_padding);
            content.setVisibility(View.GONE);
            setHeight(newHeight);
            folded = true;
        }
    }

    private void unfold() {
        if (folded) {
            headerTitle.setImageDrawable(null);
            content.setVisibility(View.VISIBLE);
            setHeight(state.getHeight());
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
            persistState(context, getCurrentState(!folded));

            detach();

            if (viewListener != null) {
                viewListener.onViewMinimized();
            }

            minimized = true;
        }
    }

    public void hide() {
        checkInit();
        if (!shown) {
            return;
        }

        persistState(context, getCurrentState(!folded));

        detach();

        if (viewListener != null) {
            viewListener.onViewHidden();
        }

        shown = false;
    }

    @Nonnull
    private WindowManager getWindowManager() {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    }

    @Nonnull
    public CalculatorOnscreenViewState getCurrentState(boolean useRealSize) {
        final WindowManager.LayoutParams params = (WindowManager.LayoutParams) root.getLayoutParams();
        if (useRealSize) {
            return CalculatorOnscreenViewState.create(params.width, params.height, params.x, params.y);
        } else {
            return CalculatorOnscreenViewState.create(state.getWidth(), state.getHeight(), params.x, params.y);
        }
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

        @Nonnull
        private final WindowManager wm;
        @Nonnull
        private final View view;
        private int orientation;
        private float x0;
        private float y0;
        private long time = 0;
        private int displayWidth;

        private int displayHeight;

    	/*
    	**********************************************************************
    	*
    	*                           CONSTRUCTORS
    	*
    	**********************************************************************
    	*/

        public WindowDragTouchListener(@Nonnull WindowManager wm,
                                       @Nonnull View view) {
            this.wm = wm;
            this.view = view;
            initDisplayParams();
        }

        @Nonnull
        private static String toString(float x, float y) {
            return "(" + formatFloat(x) + ", " + formatFloat(y) + ")";
        }

        private static String formatFloat(float value) {
            if (value >= 0) {
                return "+" + String.format("%.2f", value);
            } else {
                return String.format(Locale.ENGLISH, "%.2f", value);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (orientation != this.wm.getDefaultDisplay().getOrientation()) {
                // orientation has changed => we need to check display width/height each time window moved
                initDisplayParams();
            }

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

                    if (currentTime - time >= TIME_EPS) {
                        time = currentTime;
                        processMove(x1, y1);
                    }
                    return true;
            }

            return false;
        }

        private void initDisplayParams() {
            this.orientation = this.wm.getDefaultDisplay().getOrientation();

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);

            this.displayWidth = displayMetrics.widthPixels;
            this.displayHeight = displayMetrics.heightPixels;
        }

        private void processMove(float x1, float y1) {
            final float Δx = x1 - x0;
            final float Δy = y1 - y0;

            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
            Log.d(TAG, "0:" + toString(x0, y0) + ", 1: " + toString(x1, y1) + ", Δ: " + toString(Δx, Δy) + ", params: " + toString(params.x, params.y));

            boolean xInBounds = isDistanceInBounds(Δx);
            boolean yInBounds = isDistanceInBounds(Δy);
            if (xInBounds || yInBounds) {

                if (xInBounds) {
                    params.x = (int) (params.x + Δx);
                }

                if (yInBounds) {
                    params.y = (int) (params.y + Δy);
                }

                params.x = Math.min(Math.max(params.x, 0), displayWidth - params.width);
                params.y = Math.min(Math.max(params.y, 0), displayHeight - params.height);

                wm.updateViewLayout(view, params);

                if (xInBounds) {
                    x0 = x1;
                }

                if (yInBounds) {
                    y0 = y1;
                }
            }
        }

        private boolean isDistanceInBounds(float δx) {
            δx = Math.abs(δx);
            return δx >= DIST_EPS && δx < DIST_MAX;
        }
    }
}
