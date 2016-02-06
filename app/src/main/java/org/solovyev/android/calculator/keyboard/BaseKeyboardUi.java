package org.solovyev.android.calculator.keyboard;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.buttons.CppSpecialButton;
import org.solovyev.android.calculator.view.ScreenMetrics;
import org.solovyev.android.views.dragbutton.DirectionDragButton;
import org.solovyev.android.views.dragbutton.DragDirection;
import org.solovyev.android.views.dragbutton.SimpleDragListener;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;
import static org.solovyev.android.calculator.App.getScreenMetrics;
import static org.solovyev.android.calculator.Preferences.Gui.Layout.simple;
import static org.solovyev.android.calculator.Preferences.Gui.Layout.simple_mobile;

public abstract class BaseKeyboardUi implements SharedPreferences.OnSharedPreferenceChangeListener, SimpleDragListener.DragProcessor, View.OnClickListener {

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
    PreferredPreferences preferredPreferences;
    protected int orientation = Configuration.ORIENTATION_PORTRAIT;
    private int textSize;
    private Preferences.Gui.Layout layout;

    public BaseKeyboardUi(@NonNull Application application) {
        listener = new SimpleDragListener(this, application);
    }

    public void onCreateView(@Nonnull Activity activity, @Nonnull View view) {
        cast(activity.getApplication()).getComponent().inject(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        orientation = Views.getScreenOrientation(activity);
        layout = Preferences.Gui.layout.getPreferenceNoError(preferences);
        textSize = layout.optimized ? 0 : calculateTextSize();
    }

    protected final void prepareButton(@Nullable View button) {
        if (button == null) {
            return;
        }
        button.setOnClickListener(this);
    }

    protected final void prepareButton(@Nullable DirectionDragButton button) {
        if (button == null) {
            return;
        }
        prepareButton((View) button);
        button.setOnDragListener(listener);
        BaseUi.setFont(button, typeface);
        if (textSize > 0) {
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
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

    protected boolean isSimpleLayout() {
        return layout == simple || layout == simple_mobile;
    }

    protected final void onClick(@Nonnull View v, @Nonnull String s) {
        if (keyboard.buttonPressed(s)) {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    protected final void onClick(@Nonnull View v, @Nonnull CppSpecialButton b) {
        onClick(v, b.action);
    }
}
