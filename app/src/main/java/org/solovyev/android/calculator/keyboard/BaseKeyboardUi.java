package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.view.ScreenMetrics;
import org.solovyev.android.views.Adjuster;
import org.solovyev.android.views.dragbutton.DirectionDragButton;
import org.solovyev.android.views.dragbutton.DragButton;
import org.solovyev.android.views.dragbutton.DragDirection;
import org.solovyev.android.views.dragbutton.SimpleDragListener;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static android.view.HapticFeedbackConstants.*;
import static org.solovyev.android.calculator.App.cast;
import static org.solovyev.android.calculator.App.getScreenMetrics;
import static org.solovyev.android.calculator.Preferences.Gui.Layout.simple;
import static org.solovyev.android.calculator.Preferences.Gui.Layout.simple_mobile;

public abstract class BaseKeyboardUi implements SharedPreferences.OnSharedPreferenceChangeListener, SimpleDragListener.DragProcessor, View.OnClickListener {

    public static float getTextScale(@NonNull Context context) {
        return App.isTablet(context) ? 0.5f : 0.6f;
    }

    public static final float IMAGE_SCALE = 0.6f;
    public static final float IMAGE_SCALE_ERASE = 0.5f;

    @NonNull
    private final List<DragButton> dragButtons = new ArrayList<>();
    @NonNull
    protected final SimpleDragListener listener;
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
    PreferredPreferences preferredPreferences;
    protected int orientation = Configuration.ORIENTATION_PORTRAIT;
    private int textSize;
    private Preferences.Gui.Layout layout;
    private final float textScale;

    public BaseKeyboardUi(@NonNull Application application) {
        listener = new SimpleDragListener(this, application);
        textScale = getTextScale(application);
    }

    public static void adjustButton(@NonNull View button) {
        if (button instanceof TextView) {
            Adjuster.adjustText((TextView) button, getTextScale(button.getContext()));
        } else if (button instanceof ImageView) {
            Adjuster.adjustImage((ImageView) button, IMAGE_SCALE);
        }
    }

    public void onCreateView(@Nonnull Activity activity, @Nonnull View view) {
        cast(activity.getApplication()).getComponent().inject(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        orientation = Views.getScreenOrientation(activity);
        layout = Preferences.Gui.layout.getPreferenceNoError(preferences);
        textSize = calculateTextSize();
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
    }

    protected final void prepareButton(@Nullable DirectionDragButton button) {
        if (button == null) {
            return;
        }
        dragButtons.add(button);
        button.setVibrateOnDrag(keyboard.isVibrateOnKeypress());
        prepareButton((View) button);
        button.setOnDragListener(listener);
        BaseActivity.setFont(button, typeface);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        Adjuster.adjustText(button, textScale);
    }

    protected final void hideText(@Nullable DirectionDragButton button, @Nonnull DragDirection... directions) {
        if (button == null) {
            return;
        }
        for (DragDirection direction : directions) {
            hideText(button, direction);
        }
    }

    protected final void hideText(@Nullable DirectionDragButton button, @Nonnull DragDirection direction) {
        if (button == null) {
            return;
        }
        button.showDirectionText(false, direction);
    }

    public void onDestroyView() {
        dragButtons.clear();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public static int calculateTextSize() {
        final ScreenMetrics metrics = getScreenMetrics();
        final boolean portrait = metrics.isInPortraitMode();
        final int buttonsCount = portrait ? 5 : 4;
        final int buttonsWeight = portrait ? (2 + 1 + buttonsCount) : (2 + buttonsCount);
        final int buttonSize = metrics.getHeightPxs() / buttonsWeight;
        return 5 * buttonSize / 12;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (Preferences.Gui.vibrateOnKeypress.isSameKey(key)) {
            final boolean vibrate = Preferences.Gui.vibrateOnKeypress.getPreference(preferences);
            for (DragButton dragButton : dragButtons) {
                dragButton.setVibrateOnDrag(vibrate);
            }
        }
    }

    protected boolean isSimpleLayout() {
        return layout == simple || layout == simple_mobile;
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
}
