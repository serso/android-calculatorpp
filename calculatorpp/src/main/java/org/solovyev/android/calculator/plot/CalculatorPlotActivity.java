package org.solovyev.android.calculator.plot;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorActivityHelper;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.about.CalculatorFragmentType;

/**
 * User: serso
 * Date: 9/30/12
 * Time: 4:56 PM
 */
public class CalculatorPlotActivity extends SherlockFragmentActivity {

    @NotNull
    private final CalculatorActivityHelper activityHelper = CalculatorApplication.getInstance().createActivityHelper(R.layout.main_empty, CalculatorPlotActivity.class.getSimpleName());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper.onCreate(this, savedInstanceState);

        final Intent intent = getIntent();

        final Bundle arguments;
        if (intent != null) {
            arguments = intent.getExtras();
        } else {
            arguments = null;
        }

        activityHelper.addTab(this, CalculatorFragmentType.plotter, arguments, R.id.main_layout);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        activityHelper.onSaveInstanceState(this, outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        this.activityHelper.onPause(this);

        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        activityHelper.onDestroy(this);
    }


}
