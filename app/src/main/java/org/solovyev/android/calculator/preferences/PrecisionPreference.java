package org.solovyev.android.calculator.preferences;

import static org.solovyev.common.NumberFormatter.MAX_PRECISION;
import static org.solovyev.common.NumberFormatter.MIN_PRECISION;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.R;
import org.solovyev.android.views.DiscreteSeekBar;

@SuppressWarnings("unused")
public class PrecisionPreference extends DialogPreference {

    public static class Dialog extends PreferenceDialogFragmentCompat {
        private static final String SAVE_STATE_PRECISION = "PrecisionPreferenceDialog.precision";

        private DiscreteSeekBar seekBar;
        private int precision;

        public Dialog() {
            final Bundle args = new Bundle();
            args.putString(ARG_KEY, Engine.Preferences.Output.precision.getKey());
            setArguments(args);
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
            if (seekBar != null) {
                outState.putInt(SAVE_STATE_PRECISION, seekBar.getCurrentTick() + 1);
            }
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState == null) {
                precision = readPrecision();
            } else {
                precision = savedInstanceState.getInt(SAVE_STATE_PRECISION, readPrecision());
            }
        }

        @Override
        protected void onBindDialogView(@NonNull View view) {
            super.onBindDialogView(view);
            seekBar = (DiscreteSeekBar) view.findViewById(R.id.precision_seekbar);

            seekBar.setMax(MAX_PRECISION - 1);
            seekBar.setCurrentTick(precision - 1);
        }

        private int readPrecision() {
            final DialogPreference preference = getPreference();
            final SharedPreferences preferences = preference.getSharedPreferences();
            return Math.max(MIN_PRECISION, Math.min(MAX_PRECISION, Engine.Preferences.Output.precision.getPreference(preferences)));
        }

        @Override
        public void onDialogClosed(boolean save) {
            if (!save) return;
            final int precision = seekBar.getCurrentTick() + 1;
            final DialogPreference preference = getPreference();
            if (preference.callChangeListener(precision)) {
                final SharedPreferences preferences = preference.getSharedPreferences();
                final SharedPreferences.Editor editor = preferences.edit();
                Engine.Preferences.Output.precision.putPreference(editor, precision);
                editor.apply();
            }
        }
    }

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
}
