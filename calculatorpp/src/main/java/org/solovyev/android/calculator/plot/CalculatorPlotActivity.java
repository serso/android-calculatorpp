package org.solovyev.android.calculator.plot;

import android.content.Intent;
import android.os.Bundle;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragmentActivity;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.about.CalculatorFragmentType;

/**
 * User: serso
 * Date: 9/30/12
 * Time: 4:56 PM
 */
public class CalculatorPlotActivity extends CalculatorFragmentActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        final Bundle arguments;
        if (intent != null) {
            arguments = intent.getExtras();
        } else {
            arguments = null;
        }

        getActivityHelper().addTab(this, CalculatorFragmentType.plotter, arguments, R.id.main_layout);
    }
}
