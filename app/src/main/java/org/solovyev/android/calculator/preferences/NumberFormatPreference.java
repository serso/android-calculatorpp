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
import android.widget.Spinner;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Named;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.text.NaturalComparator;
import org.solovyev.android.views.DiscreteSeekBar;
import org.solovyev.common.NumberFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.solovyev.android.calculator.Engine.Preferences.Output;

public class NumberFormatPreference extends DialogPreference {
    @Bind(R.id.nf_notation_spinner)
    Spinner notationSpinner;
    ArrayAdapter<Named<Engine.Notation>> notationAdapter;
    @Bind(R.id.nf_precision_seekbar)
    DiscreteSeekBar precisionSeekBar;
    @Bind(R.id.nf_separator_spinner)
    Spinner separatorSpinner;
    ArrayAdapter<Named<Character>> separatorAdapter;

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
        final int maxPrecision = precisionSeekBar.getMaxTick();
        precisionSeekBar.setMax(maxPrecision);
        precisionSeekBar.setCurrentTick(Math.max(0, Math.min(maxPrecision, Output.precision.getPreference(preferences))));
        notationAdapter = makeNotationAdapter();
        notationSpinner.setAdapter(notationAdapter);
        notationSpinner.setSelection(indexOf(notationAdapter, Output.notation.getPreference(preferences)));

        separatorAdapter = makeSeparatorAdapter();
        separatorSpinner.setAdapter(separatorAdapter);
        separatorSpinner.setSelection(indexOf(separatorAdapter, Output.separator.getPreference(preferences)));
    }

    @Override
    protected void onDialogClosed(boolean save) {
        super.onDialogClosed(save);
        if (!save) {
            return;
        }
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        Output.precision.putPreference(editor, precisionSeekBar.getCurrentTick());
        Output.notation.putPreference(editor, notationAdapter.getItem(notationSpinner.getSelectedItemPosition()).item);
        Output.separator.putPreference(editor, separatorAdapter.getItem(separatorSpinner.getSelectedItemPosition()).item);
        editor.apply();
    }

    private <T> int indexOf(ArrayAdapter<Named<T>> adapter, T item) {
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).item.equals(item)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    private ArrayAdapter<Named<Engine.Notation>> makeNotationAdapter() {
        final Context context = getContext();
        final ArrayAdapter<Named<Engine.Notation>> adapter = App.makeSimpleSpinnerAdapter(context);
        for (Engine.Notation notation : Engine.Notation.values()) {
            adapter.add(Named.create(notation, notation.name, context));
        }
        adapter.sort(NaturalComparator.INSTANCE);
        return adapter;
    }

    @NonNull
    private ArrayAdapter<Named<Character>> makeSeparatorAdapter() {
        final Context context = getContext();
        final ArrayAdapter<Named<Character>> adapter = App.makeSimpleSpinnerAdapter(context);
        adapter.add(Named.create(NumberFormatter.NO_GROUPING, R.string.p_grouping_separator_no, context));
        adapter.add(Named.create('\'', R.string.p_grouping_separator_apostrophe, context));
        adapter.add(Named.create(' ', R.string.p_grouping_separator_space, context));
        return adapter;
    }
}
