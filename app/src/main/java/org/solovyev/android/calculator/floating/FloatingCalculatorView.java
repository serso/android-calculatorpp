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

package org.solovyev.android.calculator.floating;

import static android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING;
import static android.view.HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING;
import static android.view.HapticFeedbackConstants.KEYBOARD_TAP;
import static android.view.HapticFeedbackConstants.LONG_PRESS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static org.solovyev.android.calculator.App.cast;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import org.solovyev.android.calculator.AppModule;
import org.solovyev.android.calculator.DisplayState;
import org.solovyev.android.calculator.DisplayView;
import org.solovyev.android.calculator.Editor;
import org.solovyev.android.calculator.EditorState;
import org.solovyev.android.calculator.EditorView;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.buttons.CppButton;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

public class FloatingCalculatorView {

    private static class MyTouchListener implements View.OnTouchListener {
        private static final float DIST_EPS = 0f;
        private static final float DIST_MAX = 100000f;
        private static final long TIME_EPS = 0L;

        @Nonnull
        private final WindowManager wm;
        @Nonnull
        private final View view;
        private int orientation;
        private float x0;
        private float y0;
        private long lastMoveTime = 0;
        private final DisplayMetrics dm = new DisplayMetrics();

        public MyTouchListener(@Nonnull WindowManager wm,
                @Nonnull View view) {
            this.wm = wm;
            this.view = view;
            onDisplayChanged();
        }

        private void onDisplayChanged() {
            final Display dd = wm.getDefaultDisplay();
            //noinspection deprecation
            orientation = dd.getOrientation();
            dd.getMetrics(dm);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //noinspection deprecation
            if (orientation != wm.getDefaultDisplay().getOrientation()) {
                // orientation has changed => we need to check display width/height each time window moved
                onDisplayChanged();
            }

            final float x1 = event.getRawX();
            final float y1 = event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x0 = x1;
                    y0 = y1;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    final long now = System.currentTimeMillis();
                    if (now - lastMoveTime >= TIME_EPS) {
                        lastMoveTime = now;
                        processMove(x1, y1);
                    }
                    return true;
            }

            return false;
        }

        private void processMove(float x1, float y1) {
            final float Δx = x1 - x0;
            final float Δy = y1 - y0;

            final WindowManager.LayoutParams params =
                    (WindowManager.LayoutParams) view.getLayoutParams();

            boolean xInBounds = isDistanceInBounds(Δx);
            boolean yInBounds = isDistanceInBounds(Δy);
            if (xInBounds || yInBounds) {

                if (xInBounds) {
                    params.x = (int) (params.x + Δx);
                }

                if (yInBounds) {
                    params.y = (int) (params.y + Δy);
                }

                params.x = Math.min(Math.max(params.x, 0), dm.widthPixels - params.width);
                params.y = Math.min(Math.max(params.y, 0), dm.heightPixels - params.height);

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

    public static class State implements Parcelable {

        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(@Nonnull Parcel in) {
                return new State(in);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
        public final int width;
        public final int height;
        public final int x;
        public final int y;

        public State(int width, int height, int x, int y) {
            this.width = width;
            this.height = height;
            this.x = x;
            this.y = y;
        }

        private State(@NonNull SharedPreferences prefs) {
            width = prefs.getInt("width", 200);
            height = prefs.getInt("height", 400);
            x = prefs.getInt("x", 0);
            y = prefs.getInt("y", 0);
        }

        public State(@Nonnull Parcel in) {
            width = in.readInt();
            height = in.readInt();
            x = in.readInt();
            y = in.readInt();
        }

        @android.support.annotation.Nullable
        public static State fromPrefs(@NonNull SharedPreferences prefs) {
            if(!prefs.contains("width")) {
                return null;
            }
            return new State(prefs);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@Nonnull Parcel out, int flags) {
            out.writeInt(width);
            out.writeInt(height);
            out.writeInt(x);
            out.writeInt(y);
        }

        @Override
        public String toString() {
            return "State{" +
                    "y=" + y +
                    ", x=" + x +
                    ", height=" + height +
                    ", width=" + width +
                    '}';
        }

        public void save(@NonNull SharedPreferences.Editor editor) {
            editor.putInt("width", width);
            editor.putInt("height", height);
            editor.putInt("x", x);
            editor.putInt("y", y);
        }
    }
    @NonNull
    private final Context context;
    @NonNull
    private final FloatingViewListener listener;
    @Inject
    Keyboard keyboard;
    @Inject
    Editor editor;
    @Inject
    SharedPreferences preferences;
    @Named(AppModule.PREFS_FLOATING)
    @Inject
    SharedPreferences myPreferences;
    private View root;
    private View content;
    private View header;
    private ImageView headerTitle;
    private Drawable headerTitleDrawable;
    private EditorView editorView;
    private DisplayView displayView;
    @Nonnull
    private final State state;
    private boolean minimized;
    private boolean attached;
    private boolean folded;
    private boolean initialized;
    private boolean shown;

    public FloatingCalculatorView(@Nonnull Context context,
            @Nonnull State state,
            @NonNull FloatingViewListener listener) {
        cast(context).getComponent().inject(this);
        this.context = context;
        this.listener = listener;
        final Preferences.SimpleTheme theme =
                Preferences.Onscreen.theme.getPreferenceNoError(preferences);
        final Preferences.Gui.Theme appTheme =
                Preferences.Gui.theme.getPreferenceNoError(preferences);
        this.root = View.inflate(context, theme.getOnscreenLayout(appTheme), null);
        final State persistedState = State.fromPrefs(myPreferences);
        if (persistedState != null) {
            this.state = persistedState;
        } else {
            this.state = state;
        }
    }

    static boolean isOverlayPermissionGranted(@NonNull Context context) {
        try {
            final Context application = context.getApplicationContext();
            final WindowManager wm =
                    (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) {
                return false;
            }
            final View view = new View(application);
            wm.addView(view, makeLayoutParams());
            wm.removeView(view);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateDisplayState(@Nonnull DisplayState displayState) {
        checkInit();
        displayView.setState(displayState);
    }

    private void checkInit() {
        if (!initialized) {
            throw new IllegalStateException("init() must be called!");
        }
    }

    public void updateEditorState(@Nonnull EditorState editorState) {
        checkInit();
        editorView.setState(editorState);
    }

    private void setHeight(int height) {
        checkInit();

        final WindowManager.LayoutParams params =
                (WindowManager.LayoutParams) root.getLayoutParams();
        params.height = height;
        getWindowManager().updateViewLayout(root, params);
    }

    private void init() {
        if (initialized) {
            return;
        }

        for (final CppButton widgetButton : CppButton.values()) {
            final View button = root.findViewById(widgetButton.id);
            if (button == null) {
                continue;
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (keyboard.buttonPressed(widgetButton.action)) {
                        if (keyboard.isVibrateOnKeypress()) {
                            v.performHapticFeedback(KEYBOARD_TAP,
                                    FLAG_IGNORE_GLOBAL_SETTING | FLAG_IGNORE_VIEW_SETTING);
                        }
                    }
                    if (widgetButton == CppButton.app) {
                        minimize();
                    }
                }
            });
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (keyboard.buttonPressed(widgetButton.actionLong)) {
                        if (keyboard.isVibrateOnKeypress()) {
                            v.performHapticFeedback(LONG_PRESS,
                                    FLAG_IGNORE_GLOBAL_SETTING | FLAG_IGNORE_VIEW_SETTING);
                        }
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
        editorView.setEditor(editor);

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

        root.findViewById(R.id.onscreen_close_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hide();
                    }
                });

        headerTitle.setOnTouchListener(new MyTouchListener(wm, root));

        initialized = true;

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
            final WindowManager.LayoutParams params = makeLayoutParams();
            params.width = state.width;
            params.height = state.height;
            params.x = state.x;
            params.y = state.y;
            params.gravity = Gravity.TOP | Gravity.LEFT;
            wm.addView(root, params);
            attached = true;
        }
    }

    @Nonnull
    private static WindowManager.LayoutParams makeLayoutParams() {
        return new WindowManager.LayoutParams(
                TYPE_SYSTEM_ALERT,
                FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
    }

    private void fold() {
        if (!folded) {
            headerTitle.setImageDrawable(headerTitleDrawable);
            final Resources r = header.getResources();
            final int newHeight = header.getHeight() + 2 * r
                    .getDimensionPixelSize(R.dimen.cpp_onscreen_main_padding);
            content.setVisibility(View.GONE);
            setHeight(newHeight);
            folded = true;
        }
    }

    private void unfold() {
        if (folded) {
            headerTitle.setImageDrawable(null);
            content.setVisibility(View.VISIBLE);
            setHeight(state.height);
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
            saveState();
            detach();
            listener.onViewMinimized();
            minimized = true;
        }
    }

    public void hide() {
        checkInit();
        if (!shown) {
            return;
        }
        saveState();
        detach();
        listener.onViewHidden();
        shown = false;
    }

    private void saveState() {
        final SharedPreferences.Editor editor = myPreferences.edit();
        getState().save(editor);
        editor.apply();
    }

    @Nonnull
    private WindowManager getWindowManager() {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    }

    @Nonnull
    public State getState() {
        final WindowManager.LayoutParams params =
                (WindowManager.LayoutParams) root.getLayoutParams();
        if (!folded) {
            return new State(params.width, params.height, params.x, params.y);
        } else {
            return new State(state.width, state.height, params.x, params.y);
        }
    }
}
