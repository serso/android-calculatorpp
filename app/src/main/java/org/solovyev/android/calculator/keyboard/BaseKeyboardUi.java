package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import dagger.Lazy;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.memory.Memory;
import org.solovyev.android.views.Adjuster;
import org.solovyev.android.views.dragbutton.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static android.view.HapticFeedbackConstants.*;
import static org.solovyev.android.calculator.App.cast;
import static org.solovyev.android.calculator.Preferences.Gui.Mode.simple;

public abstract class BaseKeyboardUi implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    public static float getTextScale(@NonNull Context context) {
        return App.isTablet(context) ? 0.4f : 0.5f;
    }

    public static final float IMAGE_SCALE = 0.5f;
    public static final float IMAGE_SCALE_ERASE = 0.4f;

    @NonNull
    private final List<DragView> dragButtons = new ArrayList<>();
    @NonNull
    protected final DirectionDragListener listener;
    @Inject
    SharedPreferences preferences;
    @Inject
    Typeface typeface;
    @Inject
    Keyboard keyboard;
    @Inject
    Editor editor;
    @Inject
    Calculator calculator;
    @Inject
    ActivityLauncher launcher;
    @Inject
    Lazy<Memory> memory;
    @Inject
    PreferredPreferences preferredPreferences;
    protected int orientation = ORIENTATION_PORTRAIT;
    private int textSize;
    private Preferences.Gui.Mode mode;
    private final float textScale;

    public BaseKeyboardUi(@NonNull Application application) {
        listener = new DirectionDragListener(application) {
            @Override
            protected boolean onDrag(@NonNull View view, @NonNull DragEvent event, @NonNull DragDirection direction) {
                return Drag.hasDirectionText(view, direction) && BaseKeyboardUi.this.onDrag(view, direction);
            }
        };
        textScale = getTextScale(application);
    }

    public static void adjustButton(@NonNull View button) {
        if (button instanceof TextView) {
            Adjuster.adjustText((TextView) button, getTextScale(button.getContext()));
        } else if (button instanceof DirectionDragImageButton) {
            Adjuster.adjustText(((DirectionDragImageButton) button), AdjusterHelper.instance, getTextScale(button.getContext()), 0);
            Adjuster.adjustImage((ImageView) button, IMAGE_SCALE);
        } else if (button instanceof ImageView) {
            Adjuster.adjustImage((ImageView) button, IMAGE_SCALE);
        }
    }

    protected abstract boolean onDrag(@NonNull View view, @NonNull DragDirection direction);

    public void onCreateView(@Nonnull Activity activity, @Nonnull View view) {
        cast(activity.getApplication()).getComponent().inject(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        orientation = App.getScreenOrientation(activity);
        mode = Preferences.Gui.mode.getPreferenceNoError(preferences);
        textSize = calculateTextSize(activity);
    }

    protected final void prepareButton(@Nullable ImageView button) {
        prepareButton(button, IMAGE_SCALE);
    }

    protected final void prepareButton(@Nullable ImageView button, float scale) {
        if (button == null) {
            return;
        }
        prepareButton((View) button);
        Adjuster.adjustImage(button, scale);
    }

    protected final void prepareButton(@Nullable View button) {
        if (button == null) {
            return;
        }
        // we call android.view.View.performHapticFeedback(int, int) from #onClick
        button.setHapticFeedbackEnabled(false);
        button.setOnClickListener(this);
        BaseActivity.setFont(button, typeface);
    }

    protected final void prepareButton(@Nullable DirectionDragImageButton button) {
        if (button == null) {
            return;
        }
        dragButtons.add(button);
        button.setVibrateOnDrag(keyboard.isVibrateOnKeypress());
        prepareButton((ImageView) button);
        button.setOnDragListener(listener);
        button.setTextSize(textSize);
        Adjuster.adjustText(button, AdjusterHelper.instance, textScale, 0);
    }

    protected final void prepareButton(@Nullable DirectionDragButton button) {
        if (button == null) {
            return;
        }
        dragButtons.add(button);
        button.setVibrateOnDrag(keyboard.isVibrateOnKeypress());
        prepareButton((View) button);
        button.setOnDragListener(listener);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        Adjuster.adjustText(button, textScale);
    }

    protected final void hideText(@Nullable DirectionDragView button, @Nonnull DragDirection... directions) {
        if (button == null) {
            return;
        }
        for (DragDirection direction : directions) {
            hideText(button, direction);
        }
    }

    protected final void hideText(@Nullable DirectionDragView button, @Nonnull DragDirection direction) {
        if (button == null) {
            return;
        }
        button.getText(direction).setVisible(false);
    }

    public void onDestroyView() {
        dragButtons.clear();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public static int calculateTextSize(@Nonnull Activity activity) {
        final boolean portrait = App.getScreenOrientation(activity) == ORIENTATION_PORTRAIT;
        final DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int buttonsCount = portrait ? 5 : 4;
        final int buttonsWeight = portrait ? (2 + 1 + buttonsCount) : (2 + buttonsCount);
        final int buttonSize = metrics.heightPixels / buttonsWeight;
        return 5 * buttonSize / 12;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (Preferences.Gui.vibrateOnKeypress.isSameKey(key)) {
            final boolean vibrate = Preferences.Gui.vibrateOnKeypress.getPreference(preferences);
            for (DragView dragButton : dragButtons) {
                dragButton.setVibrateOnDrag(vibrate);
            }
        }
    }

    protected boolean isSimpleMode() {
        return mode == simple;
    }

    protected final void onClick(@Nonnull View v, @Nonnull String s) {
        if (!keyboard.buttonPressed(s)) {
            return;
        }
        if (!keyboard.isVibrateOnKeypress()) {
            return;
        }
        v.performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING | FLAG_IGNORE_VIEW_SETTING);
    }

    protected final void onClick(@Nonnull View v, @Nonnull CppSpecialButton b) {
        onClick(v, b.action);
    }

    private static class AdjusterHelper implements Adjuster.Helper<DirectionDragImageButton> {

        public static AdjusterHelper instance = new AdjusterHelper();

        @Override
        public void apply(@NonNull DirectionDragImageButton view, float textSize) {
            view.setTextSize(textSize);
        }

        @Override
        public float getTextSize(@NonNull DirectionDragImageButton view) {
            return view.getTextSize();
        }
    }
}
