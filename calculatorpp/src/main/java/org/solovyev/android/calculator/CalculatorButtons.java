package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.AndroidUtils2;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.view.ColorButton;
import org.solovyev.android.view.drag.DragButton;
import org.solovyev.android.view.drag.DragDirection;
import org.solovyev.android.view.drag.SimpleOnDragListener;
import org.solovyev.common.math.Point2d;

/**
 * User: serso
 * Date: 9/28/12
 * Time: 12:06 AM
 */
public final class CalculatorButtons {

    private CalculatorButtons () {
    }


    public static void processButtons(boolean fixMagicFlames,
                                      @NotNull CalculatorPreferences.Gui.Theme theme,
                                      @NotNull View root) {
        if (theme.getThemeType() == CalculatorPreferences.Gui.ThemeType.metro) {

            if (fixMagicFlames) {
                // for metro themes we should turn off magic flames
                AndroidUtils.processViewsOfType(root, ColorButton.class, new AndroidUtils.ViewProcessor<ColorButton>() {
                    @Override
                    public void process(@NotNull ColorButton colorButton) {
                        colorButton.setDrawMagicFlame(false);
                    }
                });
            }
        }
    }

    static void initMultiplicationButton(@NotNull View root) {
        final View multiplicationButton = root.findViewById(R.id.multiplicationButton);
        if ( multiplicationButton instanceof Button) {
            ((Button) multiplicationButton).setText(CalculatorLocatorImpl.getInstance().getEngine().getMultiplicationSign());
        }
    }



    public static void toggleEqualsButton(@Nullable SharedPreferences preferences,
                                          @NotNull Activity activity) {
        preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(activity) : preferences;

        final boolean large = AndroidUtils2.isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE, activity.getResources().getConfiguration());

        if (!large) {
            if (AndroidUtils.getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT
                    || !CalculatorPreferences.Gui.autoOrientation.getPreference(preferences)) {

                final DragButton equalsButton = (DragButton)activity.findViewById(R.id.equalsButton);
                if (equalsButton != null) {
                    if (CalculatorPreferences.Gui.showEqualsButton.getPreference(preferences)) {
                        equalsButton.setVisibility(View.VISIBLE);
                        final AndroidCalculatorDisplayView calculatorDisplayView = getCalculatorDisplayView();
                        if (calculatorDisplayView != null) {
                            calculatorDisplayView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    } else {
                        equalsButton.setVisibility(View.GONE);
                        // mobile phones
                        final AndroidCalculatorDisplayView calculatorDisplayView = getCalculatorDisplayView();
                        if (calculatorDisplayView != null) {
                            calculatorDisplayView.setCompoundDrawablesWithIntrinsicBounds(activity.getResources().getDrawable(R.drawable.equals9), null, null, null);
                        }
                    }
                }
            }
        }
    }

    @Nullable
    private static AndroidCalculatorDisplayView getCalculatorDisplayView() {
        return (AndroidCalculatorDisplayView) CalculatorLocatorImpl.getInstance().getDisplay().getView();
    }

    /*
    **********************************************************************
    *
    *                           STATIC CLASSES
    *
    **********************************************************************
    */

    static class RoundBracketsDragProcessor implements SimpleOnDragListener.DragProcessor {
        @Override
        public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
            final boolean result;

            if (dragDirection == DragDirection.left) {
                getKeyboard().roundBracketsButtonPressed();
                result = true;
            } else {
                result = new DigitButtonDragProcessor(getKeyboard()).processDragEvent(dragDirection, dragButton, startPoint2d, motionEvent);
            }

            return result;
        }
    }

    @NotNull
    private static CalculatorKeyboard getKeyboard() {
        return CalculatorLocatorImpl.getInstance().getKeyboard();
    }

    static class VarsDragProcessor implements SimpleOnDragListener.DragProcessor {

        @NotNull
        private Context context;

        VarsDragProcessor(Context context) {
            this.context = context;
        }

        @Override
        public boolean processDragEvent(@NotNull DragDirection dragDirection,
                                        @NotNull DragButton dragButton,
                                        @NotNull Point2d startPoint2d,
                                        @NotNull MotionEvent motionEvent) {
            boolean result = false;

            if (dragDirection == DragDirection.up) {
                CalculatorActivityLauncher.createVar(context, CalculatorLocatorImpl.getInstance().getDisplay());
                result = true;
            }

            return result;
        }
    }

    static class AngleUnitsChanger implements SimpleOnDragListener.DragProcessor {

        @NotNull
        private final DigitButtonDragProcessor processor;

        @NotNull
        private final Context context;

        AngleUnitsChanger(@NotNull Context context) {
            this.context = context;
            this.processor = new DigitButtonDragProcessor(CalculatorLocatorImpl.getInstance().getKeyboard());
        }

        @Override
        public boolean processDragEvent(@NotNull DragDirection dragDirection,
                                        @NotNull DragButton dragButton,
                                        @NotNull Point2d startPoint2d,
                                        @NotNull MotionEvent motionEvent) {
            boolean result = false;

            if (dragButton instanceof AngleUnitsButton) {
                if (dragDirection != DragDirection.left) {
                    final String directionText = ((AngleUnitsButton) dragButton).getText(dragDirection);
                    if (directionText != null) {
                        try {

                            final AngleUnit angleUnits = AngleUnit.valueOf(directionText);

                            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                            AndroidCalculatorEngine.Preferences.angleUnit.putPreference(preferences, angleUnits);

                            Toast.makeText(context, context.getString(R.string.c_angle_units_changed_to, angleUnits.name()), Toast.LENGTH_LONG).show();

                            result = true;
                        } catch (IllegalArgumentException e) {
                            Log.d(this.getClass().getName(), "Unsupported angle units: " + directionText);
                        }
                    }
                } else if (dragDirection == DragDirection.left) {
                    result = processor.processDragEvent(dragDirection, dragButton, startPoint2d, motionEvent);
                }
            }

            return result;
        }
    }

    static class NumeralBasesChanger implements SimpleOnDragListener.DragProcessor {

        @NotNull
        private final Context context;

         NumeralBasesChanger(@NotNull Context context) {
            this.context = context;
        }

        @Override
        public boolean processDragEvent(@NotNull DragDirection dragDirection,
                                        @NotNull DragButton dragButton,
                                        @NotNull Point2d startPoint2d,
                                        @NotNull MotionEvent motionEvent) {
            boolean result = false;

            if (dragButton instanceof NumeralBasesButton) {
                final String directionText = ((NumeralBasesButton) dragButton).getText(dragDirection);
                if (directionText != null) {
                    try {

                        final NumeralBase numeralBase = NumeralBase.valueOf(directionText);

                        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        AndroidCalculatorEngine.Preferences.numeralBase.putPreference(preferences, numeralBase);

                        Toast.makeText(context, context.getString(R.string.c_numeral_base_changed_to, numeralBase.name()), Toast.LENGTH_LONG).show();

                        result = true;
                    } catch (IllegalArgumentException e) {
                        Log.d(this.getClass().getName(), "Unsupported numeral base: " + directionText);
                    }
                }
            }

            return result;
        }
    }
}
