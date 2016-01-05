package org.solovyev.android.calculator.wizard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.solovyev.android.calculator.R;

import java.util.List;

import javax.annotation.Nonnull;

final class WizardArrayAdapter<T> extends ArrayAdapter<T> {

    public WizardArrayAdapter(@Nonnull Context context, @Nonnull T[] items) {
        super(context, R.layout.support_simple_spinner_dropdown_item, items);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public WizardArrayAdapter(@Nonnull Context context, @Nonnull List<T> items) {
        super(context, R.layout.support_simple_spinner_dropdown_item, items);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Nonnull
    public static WizardArrayAdapter<String> create(@Nonnull Context context, int array) {
        return new WizardArrayAdapter<>(context, context.getResources().getStringArray(array));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        if (view instanceof TextView) {
            ((TextView) view).setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
        }
        return view;
    }
}
