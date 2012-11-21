package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import org.jetbrains.annotations.NotNull;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 12:03
 */
public class CalculatorDisplayFragment extends SherlockFragment {

    @NotNull
    private CalculatorFragmentHelper fragmentHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.getLayout(prefs);
        if (layout == CalculatorPreferences.Gui.Layout.main_calculator_mobile) {
            fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.calc_display_mobile, R.string.result);
        } else {
            fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.calc_display, R.string.result);
        }

        fragmentHelper.onCreate(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentHelper.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        ((AndroidCalculator) Locator.getInstance().getCalculator()).setDisplay(getActivity());

        fragmentHelper.onViewCreated(this, root);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        fragmentHelper.onResume(this);
    }

    @Override
    public void onPause() {
        fragmentHelper.onPause(this);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        fragmentHelper.onDestroy(this);

        super.onDestroy();
    }
}
