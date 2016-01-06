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
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.solovyev.android.Views;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.calculator.view.ScreenMetrics;
import org.solovyev.android.views.dragbutton.DragButton;
import org.solovyev.android.views.dragbutton.DragDirection;
import org.solovyev.android.views.dragbutton.SimpleDragListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CalculatorButtons {

    private CalculatorButtons() {
    }


    public static void fixButtonsTextSize(@Nonnull Preferences.Gui.Theme theme,
                                          @Nonnull Preferences.Gui.Layout layout,
                                          @Nonnull View root) {
        if (!layout.isOptimized()) {

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

    static void initMultiplicationButton(@Nonnull View root) {
        final View multiplicationButton = root.findViewById(R.id.cpp_button_multiplication);
        if (multiplicationButton instanceof Button) {
            ((Button) multiplicationButton).setText(Locator.getInstance().getEngine().getMultiplicationSign());
        }
    }


    public static void toggleEqualsButton(@Nullable SharedPreferences preferences,
                                          @Nonnull Activity activity) {
        preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(activity) : preferences;

        final boolean large = App.isLargeScreen() && Preferences.Gui.getLayout(preferences).isOptimized();

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

    @Nonnull
    private static CalculatorKeyboard getKeyboard() {
        return Locator.getInstance().getKeyboard();
    }

    static class RoundBracketsDragProcessor implements SimpleDragListener.DragProcessor {

        @Nonnull
        private final DigitButtonDragProcessor upDownProcessor = new DigitButtonDragProcessor(getKeyboard());

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection, @Nonnull DragButton dragButton, @Nonnull PointF startPoint, @Nonnull MotionEvent motionEvent) {
            final boolean result;

            if (dragDirection == DragDirection.left) {
                App.getVibrator().vibrate();
                getKeyboard().roundBracketsButtonPressed();
                result = true;
            } else {
                result = upDownProcessor.processDragEvent(dragDirection, dragButton, startPoint, motionEvent);
            }

            return result;
        }
    }

    static class VarsDragProcessor implements SimpleDragListener.DragProcessor {

        @Nonnull
        private Context context;

        VarsDragProcessor(@Nonnull Context context) {
            this.context = context;
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            boolean result = false;

            if (dragDirection == DragDirection.up) {
                App.getVibrator().vibrate();
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_var_dialog, null, context);
                result = true;
            }/* else if (dragDirection == DragDirection.down) {
				Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_matrix_dialog, null, context);
				result = true;
			}*/

            return result;
        }
    }

    static class AngleUnitsChanger implements SimpleDragListener.DragProcessor {

        @Nonnull
        private final DigitButtonDragProcessor processor;

        @Nonnull
        private final Context context;

        AngleUnitsChanger(@Nonnull Context context) {
            this.context = context;
            this.processor = new DigitButtonDragProcessor(Locator.getInstance().getKeyboard());
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            boolean result = false;

            if (dragButton instanceof AngleUnitsButton) {
                if (dragDirection != DragDirection.left) {
                    final String directionText = ((AngleUnitsButton) dragButton).getText(dragDirection);
                    if (directionText != null) {
                        try {

                            final AngleUnit angleUnits = AngleUnit.valueOf(directionText);

                            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                            final AngleUnit oldAngleUnits = AndroidCalculatorEngine.Preferences.angleUnit.getPreference(preferences);
                            if (oldAngleUnits != angleUnits) {
                                App.getVibrator().vibrate();
                                Locator.getInstance().getPreferenceService().setAngleUnits(angleUnits);
                            }

                            result = true;
                        } catch (IllegalArgumentException e) {
                            Log.d(this.getClass().getName(), "Unsupported angle units: " + directionText);
                        }
                    }
                } else if (dragDirection == DragDirection.left) {
                    result = processor.processDragEvent(dragDirection, dragButton, startPoint, motionEvent);
                }
            }

            return result;
        }
    }

    static class NumeralBasesChanger implements SimpleDragListener.DragProcessor {

        @Nonnull
        private final Context context;

        NumeralBasesChanger(@Nonnull Context context) {
            this.context = context;
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            boolean result = false;

            if (dragButton instanceof NumeralBasesButton) {
                final String directionText = ((NumeralBasesButton) dragButton).getText(dragDirection);
                if (directionText != null) {
                    try {

                        final NumeralBase numeralBase = NumeralBase.valueOf(directionText);

                        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                        final NumeralBase oldNumeralBase = AndroidCalculatorEngine.Preferences.numeralBase.getPreference(preferences);
                        if (oldNumeralBase != numeralBase) {
                            App.getVibrator().vibrate();
                            Locator.getInstance().getPreferenceService().setNumeralBase(numeralBase);
                        }

                        result = true;
                    } catch (IllegalArgumentException e) {
                        Log.d(this.getClass().getName(), "Unsupported numeral base: " + directionText);
                    }
                }
            }

            return result;
        }
    }

    static class FunctionsDragProcessor implements SimpleDragListener.DragProcessor {

        @Nonnull
        private Context context;

        FunctionsDragProcessor(@Nonnull Context context) {
            this.context = context;
        }

        @Override
        public boolean processDragEvent(@Nonnull DragDirection dragDirection,
                                        @Nonnull DragButton dragButton,
                                        @Nonnull PointF startPoint,
                                        @Nonnull MotionEvent motionEvent) {
            boolean result = false;

            if (dragDirection == DragDirection.up) {
                App.getVibrator().vibrate();
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.show_create_function_dialog, null, context);
                result = true;
            }

            return result;
        }
    }
}
