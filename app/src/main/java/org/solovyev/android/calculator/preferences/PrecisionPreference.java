package org.solovyev.android.calculator.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.R;
import org.solovyev.android.views.DiscreteSeekBar;

import static org.solovyev.common.NumberFormatter.MAX_PRECISION;
import static org.solovyev.common.NumberFormatter.MIN_PRECISION;

@SuppressWarnings("unused")
public class PrecisionPreference extends DialogPreference {

    @Bind(R.id.precision_seekbar)
    DiscreteSeekBar seekBar;

    {
        setPersistent(false);
        setDialogLayoutResource(R.layout.preference_precision);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PrecisionPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PrecisionPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PrecisionPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PrecisionPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ButterKnife.bind(this, view);

        final SharedPreferences preferences = getSharedPreferences();
        seekBar.setMax(MAX_PRECISION - 1);
        final int precision = Math.max(MIN_PRECISION, Math.min(MAX_PRECISION, Engine.Preferences.Output.precision.getPreference(preferences)));
        seekBar.setCurrentTick(precision - 1);
    }

    @Override
    protected void onDialogClosed(boolean save) {
        super.onDialogClosed(save);
        if (!save) {
            return;
        }
        final int precision = seekBar.getCurrentTick() + 1;
        if (callChangeListener(precision)) {
            final SharedPreferences.Editor editor = getSharedPreferences().edit();
            Engine.Preferences.Output.precision.putPreference(editor, precision);
            editor.apply();
        }
    }
}
