/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.math.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import jscl.math.function.Function;
import jscl.math.function.IConstant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorMathRegistry;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.common.math.MathEntity;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 9:36 PM
 */
public class MathEntityRemover<T extends MathEntity> implements View.OnClickListener, DialogInterface.OnClickListener {

	@Nonnull
	private final T mathEntity;

	@Nullable
	private final DialogInterface.OnClickListener callbackOnCancel;

	private final boolean confirmed;

	@Nonnull
	private final CalculatorMathRegistry<? super T> varsRegistry;

	@Nonnull
	private Context context;

	@Nonnull
	private final Object source;

	@Nonnull
	private final Params params;

	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	private MathEntityRemover(@Nonnull T mathEntity,
							  @Nullable DialogInterface.OnClickListener callbackOnCancel,
							  boolean confirmed,
							  @Nonnull CalculatorMathRegistry<? super T> varsRegistry,
							  @Nonnull Context context,
							  @Nonnull Object source,
							  @Nonnull Params params) {
		this.mathEntity = mathEntity;
		this.callbackOnCancel = callbackOnCancel;
		this.confirmed = confirmed;
		this.varsRegistry = varsRegistry;
		this.context = context;
		this.source = source;
		this.params = params;
	}

	public static MathEntityRemover<IConstant> newConstantRemover(@Nonnull IConstant constant,
																  @Nullable DialogInterface.OnClickListener callbackOnCancel,
																  @Nonnull Context context,
																  @Nonnull Object source) {
		return new MathEntityRemover<IConstant>(constant, callbackOnCancel, false, Locator.getInstance().getEngine().getVarsRegistry(), context, source, Params.newConstantInstance());
	}

	public static MathEntityRemover<Function> newFunctionRemover(@Nonnull Function function,
																 @Nullable DialogInterface.OnClickListener callbackOnCancel,
																 @Nonnull Context context,
																 @Nonnull Object source) {
		return new MathEntityRemover<Function>(function, callbackOnCancel, false, Locator.getInstance().getEngine().getFunctionsRegistry(), context, source, Params.newFunctionInstance());
	}

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/


	public void showConfirmationDialog() {
		final TextView question = new TextView(context);
		question.setText(String.format(context.getString(params.getRemovalConfirmationQuestionResId()), mathEntity.getName()));
		question.setPadding(6, 6, 6, 6);
		final AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(true)
				.setView(question)
				.setTitle(params.getRemovalConfirmationTitleResId())
				.setNegativeButton(R.string.c_no, callbackOnCancel)
				.setPositiveButton(R.string.c_yes, new MathEntityRemover<T>(mathEntity, callbackOnCancel, true, varsRegistry, context, source, params));

		builder.create().show();
	}

	@Override
	public void onClick(@Nullable View v) {
		if (!confirmed) {
			showConfirmationDialog();
		} else {
			varsRegistry.remove(mathEntity);
			varsRegistry.save();

			Locator.getInstance().getCalculator().fireCalculatorEvent(params.getCalculatorEventType(), mathEntity, source);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		onClick(null);
	}

	/*
	**********************************************************************
	*
	*                           STATIC
	*
	**********************************************************************
	*/

	private static final class Params {

		private int removalConfirmationTitleResId;

		private int removalConfirmationQuestionResId;

		private CalculatorEventType calculatorEventType;

		private Params() {
		}

		public int getRemovalConfirmationTitleResId() {
			return removalConfirmationTitleResId;
		}

		public int getRemovalConfirmationQuestionResId() {
			return removalConfirmationQuestionResId;
		}

		public CalculatorEventType getCalculatorEventType() {
			return calculatorEventType;
		}

		private static <T extends MathEntity> Params newConstantInstance() {
			final Params result = new Params();
			result.removalConfirmationTitleResId = R.string.removal_confirmation;
			result.removalConfirmationQuestionResId = R.string.c_var_removal_confirmation_question;
			result.calculatorEventType = CalculatorEventType.constant_removed;
			return result;
		}

		private static <T extends MathEntity> Params newFunctionInstance() {
			final Params result = new Params();
			result.removalConfirmationTitleResId = R.string.removal_confirmation;
			result.removalConfirmationQuestionResId = R.string.function_removal_confirmation_question;
			result.calculatorEventType = CalculatorEventType.function_removed;
			return result;
		}
	}
}
