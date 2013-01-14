package org.solovyev.android.calculator.matrix;

import android.app.ActionBar;
import android.os.Bundle;
import org.solovyev.android.calculator.CalculatorFragmentActivity;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

/**
 * User: Solovyev_S
 * Date: 12.10.12
 * Time: 10:56
 */
public class CalculatorMatrixActivity extends CalculatorFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivityHelper().setFragment(this, CalculatorFragmentType.matrix_edit, null, R.id.main_layout);
    }
}
