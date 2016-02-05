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

package org.solovyev.android.calculator.buttons;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.solovyev.android.Views;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.DigitButtonDragProcessor;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.PreferredPreferences;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.calculator.view.ScreenMetrics;
import org.solovyev.android.views.dragbutton.DragButton;
import org.solovyev.android.views.dragbutton.DragDirection;
import org.solovyev.android.views.dragbutton.SimpleDragListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.AngleUnit;
import jscl.NumeralBase;

public final class CppButtons {

    private CppButtons() {
    }


    public static void fixButtonsTextSize(@Nonnull Preferences.Gui.Theme theme,
                                          @Nonnull Preferences.Gui.Layout layout,
                                          @Nonnull View root) {
        if (!layout.optimized) {

            final ScreenMetrics metrics = App.getScreenMetrics();
            final boolean portrait = metrics.isInPortraitMode();
            final int buttonsCount = portrait ? 5 : 4;
            final int buttonsWeight = portrait ? (2 + 1 + buttonsCount) : (2 + buttonsCount);
            final int buttonSize = metrics.getHeightPxs() / buttonsWeight;
            final int textSize = 5 * buttonSize / 12;

            Views.processViewsOfType(root, DragButton.class, new Views.ViewProcessor<DragButton>() {
                @Override
                public void process(@Nonnull DragButton button) {
                    button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }
            });
        }
    }

    public static void initMultiplicationButton(@Nonnull View root) {
        final View multiplicationButton = root.findViewById(R.id.cpp_button_multiplication);
        if (multiplicationButton instanceof Button) {
            ((Button) multiplicationButton).setText(Locator.getInstance().getEngine().getMultiplicationSign());
        }
    }


    public static void toggleEqualsButton(@Nullable SharedPreferences preferences,
                                          @Nonnull Activity activity) {
        preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(activity) : preferences;

        final boolean large = App.isLargeScreen() && Preferences.Gui.getLayout(preferences).optimized;

        if (!large) {
            if (Views.getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT
                    || !Preferences.Gui.autoOrientation.getPreference(preferences)) {

                final DragButton equalsButton = (DragButton) activity.findViewById(R.id.cpp_button_equals);
                if (equalsButton != null) {
                    if (Preferences.Gui.showEqualsButton.getPreference(preferences)) {
                        equalsButton.setVisibility(View.VISIBLE);
                    } else {
                        equalsButton.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public static class RoundBracketsDragProcessor implements SimpleDragListener.DragProcessor {

        @Nonnull
        private final Keyboard keyboard;
        @Nonnull
        private final DigitButtonDragProcessor upDownProcessor;

        public RoundBracketsDragProcessor(@Nonnull Keyboard keyboard) {
            this.keyboard = keyboard;
            this.upDownProcessor = new DigitButtonDragProcessor(keyboard);
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection direction, @Nonnull DragButton button, @Nonnull PointF startPoint, @Nonnull MotionEvent motionEvent) {
            if (direction == DragDirection.left) {
                keyboard.roundBracketsButtonPressed();
                return true;
            }

            return upDownProcessor.processDragEvent(direction, button, startPoint, motionEvent);
        }
    }

    public static class VarsDragProcessor implements SimpleDragListener.DragProcessor {

        @Nonnull
        private Context context;

        public VarsDragProcessor(@Nonnull Context context) {
            this.context = context;
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            if (dragDirection == DragDirection.up) {
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_var_dialog, null, context);
                return true;
            }

            return false;
        }
    }

    public static class AngleUnitsChanger implements SimpleDragListener.DragProcessor {

        @Nonnull
        private final DigitButtonDragProcessor processor;

        @Nonnull
        private final Context context;
        @Nonnull
        private final PreferredPreferences preferredPreferences;

        public AngleUnitsChanger(@Nonnull Context context, @Nonnull Keyboard keyboard, @Nonnull PreferredPreferences preferredPreferences) {
            this.context = context;
            this.preferredPreferences = preferredPreferences;
            this.processor = new DigitButtonDragProcessor(keyboard);
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            if (dragButton instanceof AngleUnitsButton) {
                if (dragDirection != DragDirection.left) {
                    final String directionText = ((AngleUnitsButton) dragButton).getText(dragDirection);
                    if (directionText != null) {
                        try {

                            final AngleUnit angleUnits = AngleUnit.valueOf(directionText);

                            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                            final AngleUnit oldAngleUnits = Engine.Preferences.angleUnit.getPreference(preferences);
                            if (oldAngleUnits != angleUnits) {
                                preferredPreferences.setAngleUnits(angleUnits);
                            }
                        } catch (IllegalArgumentException e) {
                            Log.d(this.getClass().getName(), "Unsupported angle units: " + directionText);
                        }
                        return true;
                    }
                } else if (dragDirection == DragDirection.left) {
                    return processor.processDragEvent(dragDirection, dragButton, startPoint, motionEvent);
                }
            }

            return false;
        }
    }

    public static class NumeralBasesChanger implements SimpleDragListener.DragProcessor {

        @Nonnull
        private final Context context;
        @Nonnull
        private final PreferredPreferences preferredPreferences;

        public NumeralBasesChanger(@Nonnull Context context, @Nonnull PreferredPreferences preferredPreferences) {
            this.context = context;
            this.preferredPreferences = preferredPreferences;
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            if (dragButton instanceof NumeralBasesButton) {
                final String directionText = ((NumeralBasesButton) dragButton).getText(dragDirection);
                if (directionText != null) {
                    try {
                        final NumeralBase numeralBase = NumeralBase.valueOf(directionText);

                        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                        final NumeralBase oldNumeralBase = Engine.Preferences.numeralBase.getPreference(preferences);
                        if (oldNumeralBase != numeralBase) {
                            preferredPreferences.setNumeralBase(numeralBase);
                        }
                    } catch (IllegalArgumentException e) {
                        Log.d(this.getClass().getName(), "Unsupported numeral base: " + directionText);
                    }
                    return true;
                }
            }

            return false;
        }
    }

    public static class FunctionsDragProcessor implements SimpleDragListener.DragProcessor {

        @Nonnull
        private Context context;

        public FunctionsDragProcessor(@Nonnull Context context) {
            this.context = context;
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            if (dragDirection == DragDirection.up) {
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_function_dialog, null, context);
                return true;
            }
            return false;
        }
    }
}
