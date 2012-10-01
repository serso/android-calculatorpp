/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorLocatorImpl;
import org.solovyev.android.calculator.CalculatorMathRegistry;
import org.solovyev.android.calculator.R;
import org.solovyev.common.math.MathEntity;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 9:36 PM
 */
class MathEntityRemover<T extends MathEntity> implements View.OnClickListener, DialogInterface.OnClickListener {

	@NotNull
	private final T mathEntity;

	@Nullable
	private final DialogInterface.OnClickListener callbackOnCancel;

	private final boolean confirmed;

	@NotNull
	private final CalculatorMathRegistry<? super T> varsRegistry;

    @NotNull
    private Context context;

    @NotNull
    private final Object source;

    public MathEntityRemover(@NotNull T mathEntity,
                             @Nullable DialogInterface.OnClickListener callbackOnCancel,
                             @NotNull CalculatorMathRegistry<? super T> varsRegistry,
                             @NotNull Context context,
                             @NotNull Object source) {
		this(mathEntity, callbackOnCancel, false, varsRegistry, context, source);
	}

	public MathEntityRemover(@NotNull T mathEntity,
                             @Nullable DialogInterface.OnClickListener callbackOnCancel,
                             boolean confirmed,
                             @NotNull CalculatorMathRegistry<? super T> varsRegistry,
                             @NotNull Context context,
                             @NotNull Object source) {
		this.mathEntity = mathEntity;
		this.callbackOnCancel = callbackOnCancel;
		this.confirmed = confirmed;
		this.varsRegistry = varsRegistry;
        this.context = context;
        this.source = source;
    }



	public void showConfirmationDialog() {
        final TextView question = new TextView(context);
		question.setText(String.format(context.getString(R.string.c_var_removal_confirmation_question), mathEntity.getName()));
		question.setPadding(6, 6, 6, 6);
		final AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(true)
				.setView(question)
				.setTitle(R.string.c_var_removal_confirmation)
				.setNegativeButton(R.string.c_no, callbackOnCancel)
				.setPositiveButton(R.string.c_yes, new MathEntityRemover<T>(mathEntity, callbackOnCancel, true, varsRegistry, context, source));

		builder.create().show();
	}

    @Override
    public void onClick(@Nullable View v) {
        if (!confirmed) {
            showConfirmationDialog();
        } else {
            varsRegistry.remove(mathEntity);
            varsRegistry.save();

            CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.constant_removed, mathEntity, source);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        onClick(null);
    }
}
