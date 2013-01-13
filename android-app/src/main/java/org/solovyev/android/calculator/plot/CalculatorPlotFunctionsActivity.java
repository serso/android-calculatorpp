package org.solovyev.android.calculator.plot;

import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.fragments.FragmentUtils;

/**
 * User: serso
 * Date: 1/13/13
 * Time: 5:05 PM
 */
public class CalculatorPlotFunctionsActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cpp_plot_functions_dialog);

        FragmentUtils.createFragment(this, CalculatorPlotFunctionsFragment.class, R.id.dialog_layout, "plot-functions");
    }
}
