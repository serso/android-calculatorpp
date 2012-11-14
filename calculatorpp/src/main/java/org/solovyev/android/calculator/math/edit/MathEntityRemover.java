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
import jscl.math.function.Function;
import jscl.math.function.IConstant;
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
public class MathEntityRemover<T extends MathEntity> implements View.OnClickListener, DialogInterface.OnClickListener {

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

    @NotNull
    private final Params params;

    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

	private MathEntityRemover(@NotNull T mathEntity,
                             @Nullable DialogInterface.OnClickListener callbackOnCancel,
                             boolean confirmed,
                             @NotNull CalculatorMathRegistry<? super T> varsRegistry,
                             @NotNull Context context,
                             @NotNull Object source,
                             @NotNull Params params) {
		this.mathEntity = mathEntity;
		this.callbackOnCancel = callbackOnCancel;
		this.confirmed = confirmed;
		this.varsRegistry = varsRegistry;
        this.context = context;
        this.source = source;
        this.params = params;
    }

    public static MathEntityRemover<IConstant> newConstantRemover(@NotNull IConstant constant,
                                                                  @Nullable DialogInterface.OnClickListener callbackOnCancel,
                                                                  @NotNull Context context,
                                                                  @NotNull Object source) {
        return new MathEntityRemover<IConstant>(constant, callbackOnCancel, false, CalculatorLocatorImpl.getInstance().getEngine().getVarsRegistry(), context, source, Params.newConstantInstance());
    }

    public static MathEntityRemover<Function> newFunctionRemover(@NotNull Function function,
                                                                  @Nullable DialogInterface.OnClickListener callbackOnCancel,
                                                                  @NotNull Context context,
                                                                  @NotNull Object source) {
        return new MathEntityRemover<Function>(function, callbackOnCancel, false, CalculatorLocatorImpl.getInstance().getEngine().getFunctionsRegistry(), context, source, Params.newFunctionInstance());
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

            CalculatorLocatorImpl.getInstance().getCalculator().fireCalculatorEvent(params.getCalculatorEventType(), mathEntity, source);
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
