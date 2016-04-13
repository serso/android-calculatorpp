package org.solovyev.android.calculator.converter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import org.solovyev.android.calculator.BaseActivity;
import org.solovyev.android.calculator.R;

public class ConverterActivity extends BaseActivity {

    public static final String EXTRA_VALUE = "value";

    public ConverterActivity() {
        super(R.layout.activity_empty, R.string.c_conversion_tool);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            final double value = extras != null ? extras.getDouble(EXTRA_VALUE) : 1d;
            final FragmentManager fm = getSupportFragmentManager();
            final FragmentTransaction t = fm.beginTransaction();
            t.add(R.id.main, ConverterFragment.create(value), "converter");
            t.commit();
        }
    }
}
