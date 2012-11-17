package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.model.AndroidCalculatorEngine;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 12:25
 */
public class CalculatorKeyboardFragment extends SherlockFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @NotNull
    private CalculatorPreferences.Gui.Theme theme;

    @NotNull
    private CalculatorFragmentHelper fragmentHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.getLayout(preferences);
        if (layout == CalculatorPreferences.Gui.Layout.main_calculator_mobile) {
            fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.calc_keyboard_mobile);
        } else {
            fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.calc_keyboard);
        }

        fragmentHelper.onCreate(this);

        preferences.registerOnSharedPreferenceChangeListener(this);

        theme = CalculatorPreferences.Gui.theme.getPreferenceNoError(preferences);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentHelper.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        fragmentHelper.onViewCreated(this, root);
    }


    @Override
    public void onResume() {
        super.onResume();

        this.fragmentHelper.onResume(this);
    }

    @Override
    public void onPause() {
        this.fragmentHelper.onPause(this);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fragmentHelper.onDestroy(this);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        preferences.unregisterOnSharedPreferenceChangeListener(this);

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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (AndroidCalculatorEngine.Preferences.numeralBase.getKey().equals(key) ||
                CalculatorPreferences.Gui.hideNumeralBaseDigits.getKey().equals(key) ) {
            NumeralBaseButtons.toggleNumericDigits(this.getActivity(), preferences);
        }

        if ( AndroidCalculatorEngine.Preferences.angleUnit.getKey().equals(key) ||
                AndroidCalculatorEngine.Preferences.numeralBase.getKey().equals(key) ) {
            View view = getView();
            if ( view != null) {
                // we should update state of angle units/numeral base button => we can achieve it by invalidating the whole view
                view.invalidate();
            }
        }

        if ( CalculatorPreferences.Gui.showEqualsButton.getKey().equals(key) ) {
            CalculatorButtons.toggleEqualsButton(preferences, this.getActivity());
        }

        if ( AndroidCalculatorEngine.Preferences.multiplicationSign.getKey().equals(key) ) {
            CalculatorButtons.initMultiplicationButton(getView());
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
}

