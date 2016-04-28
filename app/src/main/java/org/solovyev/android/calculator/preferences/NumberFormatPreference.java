package org.solovyev.android.calculator.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.text.NaturalComparator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NumberFormatPreference extends DialogPreference {
    @Bind(R.id.nf_notation_spinner)
    Spinner notationSpinner;
    @Bind(R.id.nf_precision_seekbar)
    SeekBar precisionSeekBar;

    {
        setPersistent(false);
        setDialogLayoutResource(R.layout.preference_number_format);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberFormatPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NumberFormatPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NumberFormatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumberFormatPreference(Context context) {
        super(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ButterKnife.bind(this, view);

        final SharedPreferences preferences = getSharedPreferences();
        precisionSeekBar.setMax(15);
        precisionSeekBar.setProgress(Math.max(0, Math.min(15, Engine.Preferences.Output.precision.getPreference(preferences))));
        final ArrayAdapter<NotationItem> adapter = makeNumberFormatAdapter();
        notationSpinner.setAdapter(adapter);
        notationSpinner.setSelection(indexOf(adapter, Engine.Preferences.Output.notation.getPreference(preferences)));
    }

    private int indexOf(ArrayAdapter<NotationItem> adapter, Engine.Notation notation) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).notation == notation) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    private ArrayAdapter<NotationItem> makeNumberFormatAdapter() {
        final ArrayAdapter<NotationItem> adapter = App.makeSimpleSpinnerAdapter(getContext());
        for (Engine.Notation format : Engine.Notation.values()) {
            adapter.add(new NotationItem(format));
        }
        adapter.sort(NaturalComparator.INSTANCE);
        return adapter;
    }

    private final class NotationItem {
        @NonNull
        final Engine.Notation notation;
        @NonNull
        final String name;

        private NotationItem(@NonNull Engine.Notation notation) {
            this.notation = notation;
            this.name = getContext().getString(notation.name);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
