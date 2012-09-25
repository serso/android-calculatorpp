package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import jscl.AngleUnit;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.history.CalculatorHistoryState;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;
import org.solovyev.android.calculator.view.AngleUnitsButton;
import org.solovyev.android.calculator.view.NumeralBasesButton;
import org.solovyev.android.calculator.view.OnDragListenerVibrator;
import org.solovyev.android.history.HistoryDragProcessor;
import org.solovyev.android.view.ColorButton;
import org.solovyev.android.view.drag.*;
import org.solovyev.common.Announcer;
import org.solovyev.common.math.Point2d;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 12:25
 */
public class CalculatorKeyboardFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Nullable
    private Vibrator vibrator;

    @NotNull
    private final Announcer<DragPreferencesChangeListener> dpclRegister = new Announcer<DragPreferencesChangeListener>(DragPreferencesChangeListener.class);

    @NotNull
    private NumeralBaseButtons numeralBaseButtons = new NumeralBaseButtons();

    @NotNull
    private CalculatorPreferences.Gui.Theme theme;

    @NotNull
    private CalculatorPreferences.Gui.Layout layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) this.getActivity().getSystemService(Activity.VIBRATOR_SERVICE);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        preferences.registerOnSharedPreferenceChangeListener(this);

        layout = CalculatorPreferences.Gui.layout.getPreferenceNoError(preferences);
        theme = CalculatorPreferences.Gui.theme.getPreferenceNoError(preferences);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.calc_keyboard, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        dpclRegister.clear();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        final SimpleOnDragListener.Preferences dragPreferences = SimpleOnDragListener.getPreferences(preferences, this.getActivity());

        setOnDragListeners(root, dragPreferences, preferences);

        final OnDragListener historyOnDragListener = new OnDragListenerVibrator(newOnDragListener(new HistoryDragProcessor<CalculatorHistoryState>(getCalculator()), dragPreferences), vibrator, preferences);
        final DragButton historyButton = getButton(root, R.id.historyButton);
        if (historyButton != null) {
            historyButton.setOnDragListener(historyOnDragListener);
        }

        final DragButton subtractionButton = getButton(root, R.id.subtractionButton);
        if (subtractionButton != null) {
            subtractionButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new SimpleOnDragListener.DragProcessor() {
                @Override
                public boolean processDragEvent(@NotNull DragDirection dragDirection, @NotNull DragButton dragButton, @NotNull Point2d startPoint2d, @NotNull MotionEvent motionEvent) {
                    if (dragDirection == DragDirection.down) {
                        CalculatorActivity.operatorsButtonClickHandler(getActivity());
                        return true;
                    }
                    return false;
                }
            }, dragPreferences), vibrator, preferences));
        }


        final OnDragListener toPositionOnDragListener = new OnDragListenerVibrator(new SimpleOnDragListener(new CursorDragProcessor(), dragPreferences), vibrator, preferences);

        final DragButton rightButton = getButton(root, R.id.rightButton);
        if (rightButton != null) {
            rightButton.setOnDragListener(toPositionOnDragListener);
        }

        final DragButton leftButton = getButton(root, R.id.leftButton);
        if (leftButton != null) {
            leftButton.setOnDragListener(toPositionOnDragListener);
        }

        final DragButton equalsButton = getButton(root, R.id.equalsButton);
        if (equalsButton != null) {
            equalsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new EvalDragProcessor(), dragPreferences), vibrator, preferences));
        }

        final AngleUnitsButton angleUnitsButton = (AngleUnitsButton) getButton(root, R.id.sixDigitButton);
        if (angleUnitsButton != null) {
            angleUnitsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new AngleUnitsChanger(this.getActivity()), dragPreferences), vibrator, preferences));
        }

        final NumeralBasesButton clearButton = (NumeralBasesButton) getButton(root, R.id.clearButton);
        if (clearButton != null) {
            clearButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new NumeralBasesChanger(this.getActivity()), dragPreferences), vibrator, preferences));
        }

        final DragButton varsButton = getButton(root, R.id.varsButton);
        if (varsButton != null) {
            varsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new VarsDragProcessor(this.getActivity()), dragPreferences), vibrator, preferences));
        }

        final DragButton roundBracketsButton = getButton(root, R.id.roundBracketsButton);
        if (roundBracketsButton != null) {
            roundBracketsButton.setOnDragListener(new OnDragListenerVibrator(newOnDragListener(new RoundBracketsDragProcessor(), dragPreferences), vibrator, preferences));
        }

        if (layout == CalculatorPreferences.Gui.Layout.simple) {
            toggleButtonDirectionText(root, R.id.oneDigitButton, false, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(root, R.id.twoDigitButton, false, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(root, R.id.threeDigitButton, false, DragDirection.up, DragDirection.down);

            toggleButtonDirectionText(root, R.id.sixDigitButton, false, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(root, R.id.sevenDigitButton, false, DragDirection.left, DragDirection.up, DragDirection.down);
            toggleButtonDirectionText(root, R.id.eightDigitButton, false, DragDirection.left, DragDirection.up, DragDirection.down);

            toggleButtonDirectionText(root, R.id.clearButton, false, DragDirection.left, DragDirection.up, DragDirection.down);

            toggleButtonDirectionText(root, R.id.fourDigitButton, false, DragDirection.down);
            toggleButtonDirectionText(root, R.id.fiveDigitButton, false, DragDirection.down);

            toggleButtonDirectionText(root, R.id.nineDigitButton, false, DragDirection.left);

            toggleButtonDirectionText(root, R.id.multiplicationButton, false, DragDirection.left);
            toggleButtonDirectionText(root, R.id.plusButton, false, DragDirection.down, DragDirection.up);
        }

        numeralBaseButtons.toggleNumericDigits(this.getActivity(), preferences);

        fixThemeParameters(true, theme, this.getView());

        toggleEqualsButton(preferences, this.getActivity(), theme, root);

        initMultiplicationButton();
    }

    @Nullable
    private <T extends DragButton> T getButton(@NotNull View root, int buttonId) {
        return (T) root.findViewById(buttonId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public static void fixThemeParameters(boolean fixMagicFlames,
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

    private void initMultiplicationButton() {
        final View multiplicationButton = getView().findViewById(R.id.multiplicationButton);
        if ( multiplicationButton instanceof Button) {
            ((Button) multiplicationButton).setText(CalculatorLocatorImpl.getInstance().getEngine().getMultiplicationSign());
        }
    }

/*    private static void setMarginsForView(@Nullable View view, int marginLeft, int marginBottom, @NotNull Context context) {
        // IMPORTANT: this is workaround for probably android bug
        // currently margin values set in styles are not applied for some reasons to the views (using include tag) => set them manually

        if (view != null) {
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();
            if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                final LinearLayout.LayoutParams oldParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                final LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(oldParams.width, oldParams.height, oldParams.weight);
                newParams.setMargins(AndroidUtils.toPixels(dm, marginLeft), 0, 0, AndroidUtils.toPixels(dm, marginBottom));
                view.setLayoutParams(newParams);
            }
        }
    }*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private synchronized void setOnDragListeners(@NotNull View root, @NotNull SimpleOnDragListener.Preferences dragPreferences, @NotNull SharedPreferences preferences) {
        final OnDragListener onDragListener = new OnDragListenerVibrator(newOnDragListener(new DigitButtonDragProcessor(getKeyboard()), dragPreferences), vibrator, preferences);

        final List<Integer> dragButtonIds = new ArrayList<Integer>();
        final List<Integer> buttonIds = new ArrayList<Integer>();

        for (Field field : R.id.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                try {
                    int viewId = field.getInt(R.id.class);
                    final View view = root.findViewById(viewId);
                    if (view instanceof DragButton) {
                        dragButtonIds.add(viewId);
                    }
                    if (view instanceof Button) {
                        buttonIds.add(viewId);
                    }
                } catch (IllegalAccessException e) {
                    Log.e(R.id.class.getName(), e.getMessage());
                }
            }
        }

        for (Integer dragButtonId : dragButtonIds) {
            final DragButton button = getButton(root, dragButtonId);
            if (button != null) {
                button.setOnDragListener(onDragListener);
            }
        }
    }


    @NotNull
    private SimpleOnDragListener newOnDragListener(@NotNull SimpleOnDragListener.DragProcessor dragProcessor,
                                                   @NotNull SimpleOnDragListener.Preferences dragPreferences) {
        final SimpleOnDragListener onDragListener = new SimpleOnDragListener(dragProcessor, dragPreferences);
        dpclRegister.addListener(onDragListener);
        return onDragListener;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key != null && key.startsWith("org.solovyev.android.calculator.DragButtonCalibrationActivity")) {
            dpclRegister.announce().onDragPreferencesChange(SimpleOnDragListener.getPreferences(preferences, this.getActivity()));
        }

        if (AndroidCalculatorEngine.Preferences.numeralBase.getKey().equals(key)) {
            numeralBaseButtons.toggleNumericDigits(this.getActivity(), preferences);
        }

        if ( CalculatorPreferences.Gui.showEqualsButton.getKey().equals(key) ) {
            toggleEqualsButton(preferences, this.getActivity(), theme, getView());
        }

        if ( AndroidCalculatorEngine.Preferences.multiplicationSign.getKey().equals(key) ) {
            initMultiplicationButton();
        }
    }

    private void toggleButtonDirectionText(@NotNull View root, int id, boolean showDirectionText, @NotNull DragDirection... dragDirections) {
        final View v = getButton(root, id);
        if (v instanceof DirectionDragButton ) {
            final DirectionDragButton button = (DirectionDragButton)v;
            for (DragDirection dragDirection : dragDirections) {
                button.showDirectionText(showDirectionText, dragDirection);
            }
        }
    }

    public static void toggleEqualsButton(@Nullable SharedPreferences preferences,
                                           @NotNull Activity activity,
                                           @NotNull CalculatorPreferences.Gui.Theme theme,
                                           @NotNull View root) {
        preferences = preferences == null ? PreferenceManager.getDefaultSharedPreferences(activity) : preferences;

        if (AndroidUtils.getScreenOrientation(activity) == Configuration.ORIENTATION_PORTRAIT || !CalculatorPreferences.Gui.autoOrientation.getPreference(preferences)) {
            final Display display = activity.getWindowManager().getDefaultDisplay();

            final DragButton button = (DragButton)activity.findViewById(R.id.equalsButton);
            if (CalculatorPreferences.Gui.showEqualsButton.getPreference(preferences)) {
                button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 1f));
                if (display.getWidth() <= 480) {
                    // mobile phones
                    final AndroidCalculatorDisplayView calculatorDisplayView = getCalculatorDisplayView();
                    if (calculatorDisplayView != null) {
                        calculatorDisplayView.setBackgroundDrawable(null);
                    }
                }
            } else {
                button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 0f));
                if (display.getWidth() <= 480) {
                    // mobile phones
                    final AndroidCalculatorDisplayView calculatorDisplayView = getCalculatorDisplayView();
                    if (calculatorDisplayView != null) {
                        calculatorDisplayView.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.equals9));
                    }
                }
            }

            fixThemeParameters(false, theme, root);
        }
    }


    @Nullable
    private static AndroidCalculatorDisplayView getCalculatorDisplayView() {
        return (AndroidCalculatorDisplayView) CalculatorLocatorImpl.getInstance().getDisplay().getView();
    }

    @NotNull
    private Calculator getCalculator() {
        return CalculatorLocatorImpl.getInstance().getCalculator();
    }

    @NotNull
    private static CalculatorKeyboard getKeyboard() {
        return CalculatorLocatorImpl.getInstance().getKeyboard();
    }

    /*
    **********************************************************************
    *
    *                           STATIC CLASSES
    *
    **********************************************************************
    */

    private static class RoundBracketsDragProcessor implements SimpleOnDragListener.DragProcessor {
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

    private static class VarsDragProcessor implements SimpleOnDragListener.DragProcessor {

        @NotNull
        private Context context;

        private VarsDragProcessor(Context context) {
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

    private static class AngleUnitsChanger implements SimpleOnDragListener.DragProcessor {

        @NotNull
        private final DigitButtonDragProcessor processor;

        @NotNull
        private final Context context;

        private AngleUnitsChanger(@NotNull Context context) {
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

    private static class NumeralBasesChanger implements SimpleOnDragListener.DragProcessor {

        @NotNull
        private final Context context;

        private NumeralBasesChanger(@NotNull Context context) {
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

