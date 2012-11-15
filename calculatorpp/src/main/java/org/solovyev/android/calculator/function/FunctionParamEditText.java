package org.solovyev.android.calculator.function;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.AbsSavedState;
import android.widget.EditText;

public class FunctionParamEditText extends EditText {

	public FunctionParamEditText(Context context) {
		super(context);
	}

	public FunctionParamEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FunctionParamEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// we restore state manually outside
	@Override
	public Parcelable onSaveInstanceState() {
		super.onSaveInstanceState();
		return AbsSavedState.EMPTY_STATE;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		super.onRestoreInstanceState(null);
	}
}
