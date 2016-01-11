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

package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.history.HistoryDragProcessor;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.LongClickEraser;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.calculator.view.ViewsCache;
import org.solovyev.android.views.dragbutton.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.solovyev.android.calculator.Preferences.Gui.Layout.simple;
import static org.solovyev.android.calculator.Preferences.Gui.Layout.simple_mobile;
import static org.solovyev.android.calculator.model.AndroidCalculatorEngine.Preferences.angleUnit;
import static org.solovyev.android.calculator.model.AndroidCalculatorEngine.Preferences.numeralBase;

public abstract class BaseUi implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nonnull
    private static final List<Integer> viewIds = new ArrayList<>(200);

    @Nonnull
    protected Preferences.Gui.Layout layout;

    @Nonnull
    protected Preferences.Gui.Theme theme;

    @Nonnull
    private String logTag = "CalculatorActivity";

    @Nullable
    private AngleUnitsButton angleUnitsButton;

    @Nullable
    private NumeralBasesButton clearButton;

    protected BaseUi() {
    }

    protected BaseUi(@Nonnull String logTag) {
        this.logTag = logTag;
    }

    @Nonnull
    private static List<Integer> getViewIds() {
        if (viewIds.isEmpty()) {
            viewIds.add(R.id.wizard_dragbutton);
            viewIds.add(R.id.cpp_button_vars);
            viewIds.add(R.id.cpp_button_round_brackets);
            viewIds.add(R.id.cpp_button_right);
            viewIds.add(R.id.cpp_button_plus);
            viewIds.add(R.id.cpp_button_operators);
            viewIds.add(R.id.cpp_button_multiplication);
            viewIds.add(R.id.cpp_button_subtraction);
            viewIds.add(R.id.cpp_button_left);
            viewIds.add(R.id.cpp_button_history);
            viewIds.add(R.id.cpp_button_functions);
            viewIds.add(R.id.cpp_button_equals);
            viewIds.add(R.id.cpp_button_period);
            viewIds.add(R.id.cpp_button_division);
            viewIds.add(R.id.cpp_button_9);
            viewIds.add(R.id.cpp_button_8);
            viewIds.add(R.id.cpp_button_7);
            viewIds.add(R.id.cpp_button_6);
            viewIds.add(R.id.cpp_button_5);
            viewIds.add(R.id.cpp_button_4);
            viewIds.add(R.id.cpp_button_3);
            viewIds.add(R.id.cpp_button_2);
            viewIds.add(R.id.cpp_button_1);
            viewIds.add(R.id.cpp_button_0);
            viewIds.add(R.id.cpp_button_clear);
        }
        return viewIds;
    }

    protected void onCreate(@Nonnull Activity activity) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        layout = Preferences.Gui.layout.getPreferenceNoError(preferences);
        theme = Preferences.Gui.theme.getPreferenceNoError(preferences);

        preferences.registerOnSharedPreferenceChangeListener(this);

        // let's disable locking of screen for monkeyrunner
        if (App.isMonkeyRunner(activity)) {
            final KeyguardManager km = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
            //noinspection deprecation
            km.newKeyguardLock(activity.getClass().getName()).disableKeyguard();
        }
    }

    public void logError(@Nonnull String message) {
        Log.e(logTag, message);
    }

    public void onDestroy(@Nonnull Activity activity) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    protected void fixFonts(@Nonnull View root) {
        // some devices ship own fonts which causes issues with rendering. Let's use our own font for all text views
        final Typeface typeFace = App.getTypeFace();
        Views.processViewsOfType(root, TextView.class, new Views.ViewProcessor<TextView>() {
            @Override
            public void process(@Nonnull TextView view) {
                int style = Typeface.NORMAL;
                final Typeface oldTypeface = view.getTypeface();
                if (oldTypeface != null) {
                    style = oldTypeface.getStyle();
                }
                view.setTypeface(typeFace, style);
            }
        });
    }

    public void processButtons(@Nonnull final Activity activity, @Nonnull View root) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        final ViewsCache views = ViewsCache.forView(root);
        setOnDragListeners(views, activity);

        HistoryDragProcessor historyDragProcessor = new HistoryDragProcessor();
        final DragListener historyDragListener = newDragListener(historyDragProcessor, activity);
        final DragButton historyButton = getButton(views, R.id.cpp_button_history);
        if (historyButton != null) {
            historyButton.setOnDragListener(historyDragListener);
        }

        final DragButton minusButton = getButton(views, R.id.cpp_button_subtraction);
        if (minusButton != null) {
            minusButton.setOnDragListener(newDragListener(new OperatorsDragProcessor(), activity));
        }

        final DragListener toPositionDragListener = new SimpleDragListener(new CursorDragProcessor(), activity);

        final DragButton rightButton = getButton(views, R.id.cpp_button_right);
        if (rightButton != null) {
            rightButton.setOnDragListener(toPositionDragListener);
        }

        final DragButton leftButton = getButton(views, R.id.cpp_button_left);
        if (leftButton != null) {
            leftButton.setOnDragListener(toPositionDragListener);
        }

        final DragButton equalsButton = getButton(views, R.id.cpp_button_equals);
        if (equalsButton != null) {
            equalsButton.setOnDragListener(newDragListener(new EqualsDragProcessor(), activity));
        }

        angleUnitsButton = getButton(views, R.id.cpp_button_6);
        if (angleUnitsButton != null) {
            angleUnitsButton.setOnDragListener(newDragListener(new CalculatorButtons.AngleUnitsChanger(activity), activity));
        }

        final View eraseButton = getButton(views, R.id.cpp_button_erase);
        if (eraseButton != null) {
            LongClickEraser.createAndAttach(eraseButton);
        }

        clearButton = getButton(views, R.id.cpp_button_clear);
        if (clearButton != null) {
            clearButton.setOnDragListener(newDragListener(new CalculatorButtons.NumeralBasesChanger(activity), activity));
        }

        final DragButton varsButton = getButton(views, R.id.cpp_button_vars);
        if (varsButton != null) {
            varsButton.setOnDragListener(newDragListener(new CalculatorButtons.VarsDragProcessor(activity), activity));
        }

        final DragButton functionsButton = getButton(views, R.id.cpp_button_functions);
        if (functionsButton != null) {
            functionsButton.setOnDragListener(newDragListener(new CalculatorButtons.FunctionsDragProcessor(activity), activity));
        }

        final DragButton roundBracketsButton = getButton(views, R.id.cpp_button_round_brackets);
        if (roundBracketsButton != null) {
            roundBracketsButton.setOnDragListener(newDragListener(new CalculatorButtons.RoundBracketsDragProcessor(), activity));
        }

        if (layout == simple || layout == simple_mobile) {
            toggleButtonDirectionText(views, R.id.cpp_button_1, false, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(views, R.id.cpp_button_2, false, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(views, R.id.cpp_button_3, false, DragDirection.up, DragDirection.down);

            toggleButtonDirectionText(views, R.id.cpp_button_6, false, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(views, R.id.cpp_button_7, false, DragDirection.left, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(views, R.id.cpp_button_8, false, DragDirection.left, DragDirection.up, DragDirection.down);

            toggleButtonDirectionText(views, R.id.cpp_button_clear, false, DragDirection.left, DragDirection.up, DragDirection.down);

            toggleButtonDirectionText(views, R.id.cpp_button_4, false, DragDirection.down);
            toggleButtonDirectionText(views, R.id.cpp_button_5, false, DragDirection.down);

            toggleButtonDirectionText(views, R.id.cpp_button_9, false, DragDirection.left);

            toggleButtonDirectionText(views, R.id.cpp_button_multiplication, false, DragDirection.left);
            toggleButtonDirectionText(views, R.id.cpp_button_plus, false, DragDirection.down, DragDirection.up);
        }

        CalculatorButtons.fixButtonsTextSize(theme, layout, root);
        CalculatorButtons.toggleEqualsButton(preferences, activity);
        CalculatorButtons.initMultiplicationButton(root);
        NumeralBaseButtons.toggleNumericDigits(activity, preferences);

        new ButtonOnClickListener().attachToViews(views);
    }

    private void setOnDragListeners(@Nonnull ViewsCache views, @Nonnull Context context) {
        final DragListener dragListener = newDragListener(new DigitButtonDragProcessor(getKeyboard()), context);

        final List<Integer> viewIds = getViewIds();
        for (Integer viewId : viewIds) {
            final View view = views.findViewById(viewId);
            if (view instanceof DragButton) {
                ((DragButton) view).setOnDragListener(dragListener);
            }
        }
    }

    @Nonnull
    private SimpleDragListener newDragListener(@Nonnull SimpleDragListener.DragProcessor dragProcessor, @Nonnull Context context) {
        return new SimpleDragListener(dragProcessor, context);
    }

    private void toggleButtonDirectionText(@Nonnull ViewsCache views, int id, boolean showDirectionText, @Nonnull DragDirection... dragDirections) {
        final View v = getButton(views, id);
        if (v instanceof DirectionDragButton) {
            final DirectionDragButton button = (DirectionDragButton) v;
            for (DragDirection dragDirection : dragDirections) {
                button.showDirectionText(showDirectionText, dragDirection);
            }
        }
    }

    @Nonnull
    private Calculator getCalculator() {
        return Locator.getInstance().getCalculator();
    }

    @Nonnull
    private CalculatorKeyboard getKeyboard() {
        return Locator.getInstance().getKeyboard();
    }

    @Nullable
    private <V extends View> V getButton(@Nonnull ViewsCache views, int buttonId) {
        //noinspection unchecked
        return (V) views.findViewById(buttonId);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (angleUnit.isSameKey(key) || numeralBase.isSameKey(key)) {
            if (angleUnitsButton != null) {
                angleUnitsButton.setAngleUnit(angleUnit.getPreference(preferences));
            }

            if (clearButton != null) {
                clearButton.setNumeralBase(numeralBase.getPreference(preferences));
            }
        }
    }

    private static class OperatorsDragProcessor implements SimpleDragListener.DragProcessor {
        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull PointF startPoint, @Nonnull MotionEvent motionEvent) {
            if (dragDirection == DragDirection.down) {
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_operators, null);
                return true;
            }
            return false;
        }
    }
}
