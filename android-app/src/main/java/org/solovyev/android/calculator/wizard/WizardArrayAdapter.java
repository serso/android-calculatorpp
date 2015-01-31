package org.solovyev.android.calculator.wizard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

final class WizardArrayAdapter extends ArrayAdapter<String> {

	public WizardArrayAdapter(Context context, int array) {
		super(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(array));
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
