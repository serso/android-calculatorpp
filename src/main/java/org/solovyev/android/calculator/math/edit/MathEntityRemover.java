/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.model.AndroidMathRegistry;
import org.solovyev.common.math.MathEntity;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 9:36 PM
 */
class MathEntityRemover<T extends MathEntity> implements DialogInterface.OnClickListener {

	@NotNull
	private final T mathEntity;

	@Nullable
	private final DialogInterface.OnClickListener callbackOnCancel;

	private final boolean confirmed;

	@NotNull
	private final AndroidMathRegistry<? super T> varsRegistry;

	@NotNull
	private final AbstractMathEntityListActivity<T> activity;

	public MathEntityRemover(@NotNull T mathEntity,
							 @Nullable DialogInterface.OnClickListener callbackOnCancel,
							 @NotNull AndroidMathRegistry<? super T> varsRegistry,
							 @NotNull AbstractMathEntityListActivity<T> activity) {
		this(mathEntity, callbackOnCancel, false, varsRegistry, activity);
	}

	public MathEntityRemover(@NotNull T mathEntity,
							 @Nullable DialogInterface.OnClickListener callbackOnCancel,
							 boolean confirmed,
							 @NotNull AndroidMathRegistry<? super T> varsRegistry,
							 @NotNull AbstractMathEntityListActivity<T> activity) {
		this.mathEntity = mathEntity;
		this.callbackOnCancel = callbackOnCancel;
		this.confirmed = confirmed;
		this.varsRegistry = varsRegistry;
		this.activity = activity;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (!confirmed) {
			showConfirmationDialog();
		} else {
			if (activity.isInCategory(mathEntity)) {
				activity.getAdapter().remove(mathEntity);
			}

			varsRegistry.remove(mathEntity);
			varsRegistry.save(activity);
			if (activity.isInCategory(mathEntity)) {
				activity.getAdapter().notifyDataSetChanged();
			}
		}
	}

	public void showConfirmationDialog() {
		final TextView question = new TextView(activity);
		question.setText(String.format(activity.getString(R.string.c_var_removal_confirmation_question), mathEntity.getName()));
		question.setPadding(6, 6, 6, 6);
		final AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setCancelable(true)
				.setView(question)
				.setTitle(R.string.c_var_removal_confirmation)
				.setNegativeButton(R.string.c_no, callbackOnCancel)
				.setPositiveButton(R.string.c_yes, new MathEntityRemover<T>(mathEntity, callbackOnCancel, true, varsRegistry, activity));

		builder.create().show();
	}
}
