/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import jscl.math.Generic;
import jscl.math.function.Constant;
import jscl.math.function.IConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.jscl.JsclOperation;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.android.calculator.model.CalculatorParseException;
import org.solovyev.android.calculator.model.TextProcessor;
import org.solovyev.android.calculator.view.TextHighlighter;
import org.solovyev.android.menu.AMenuItem;
import org.solovyev.android.view.AutoResizeTextView;

import java.util.HashSet;
import java.util.Set;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 10:58 PM
 */
public class CalculatorDisplay extends AutoResizeTextView implements ICalculatorDisplay{

	public static enum Menu implements AMenuItem<CalculatorDisplay> {
		to_bin(0) {
			@Override
			public void doAction(@NotNull CalculatorDisplay data, @NotNull Context context) {
				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
				return false;
			}
		},

		plot(R.string.c_plot_graph) {
			@Override
			public void doAction(@NotNull CalculatorDisplay data, @NotNull Context context) {

				//To change body of implemented methods use File | Settings | File Templates.
			}

			@Override
			protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
				boolean result = false;

				if (operation == JsclOperation.simplify) {
					final Set<Constant> notSystemConstants = new HashSet<Constant>();
					for (Constant constant : generic.getConstants()) {
						IConstant var = CalculatorEngine.instance.getVarsRegistry().get(constant.getName());
						if (var != null && !var.isSystem() && !var.isDefined()) {
							notSystemConstants.add(constant);
						}
					}

					if (notSystemConstants.size() == 1) {
						result = true;
					}
				}

				return result;
			}
		};

		private final int captionId;

		Menu(int captionId) {
			this.captionId = captionId;
		}

		public final boolean isItemVisible(@NotNull CalculatorDisplay display) {
			//noinspection ConstantConditions
			return display.isValid() && display.getGenericResult() != null && isItemVisibleFor(display.getGenericResult(), display.getJsclOperation());
		}

		protected boolean isItemVisibleFor(@NotNull Generic generic, @NotNull JsclOperation operation) {
			return true;
		}

		@NotNull
		@Override
		public String getCaption(@NotNull Context context) {
			return context.getString(captionId);
		}
	}

	private boolean valid = true;

	@Nullable
	private String errorMessage;

	@NotNull
	private JsclOperation jsclOperation = JsclOperation.numeric;

	@NotNull
	private final static TextProcessor<TextHighlighter.Result, String> textHighlighter = new TextHighlighter(Color.WHITE, false, CalculatorEngine.instance.getEngine());

	@Nullable
	private Generic genericResult;

	public CalculatorDisplay(Context context) {
		super(context);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalculatorDisplay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public void setValid(boolean valid) {
		this.valid = valid;
		if (valid) {
			errorMessage = null;
		}
	}

	@Override
	@Nullable
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public void setErrorMessage(@Nullable String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public void setJsclOperation(@NotNull JsclOperation jsclOperation) {
		this.jsclOperation = jsclOperation;
	}

	@Override
	@NotNull
	public JsclOperation getJsclOperation() {
		return jsclOperation;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);

		setValid(true);
	}

	public synchronized void redraw() {
		if (isValid()) {
			String text = getText().toString();

			Log.d(this.getClass().getName(), text);

			try {
				TextHighlighter.Result result = textHighlighter.process(text);
				text = result.toString();
			} catch (CalculatorParseException e) {
				Log.e(this.getClass().getName(), e.getMessage(), e);
			}

			Log.d(this.getClass().getName(), text);
			super.setText(Html.fromHtml(text), BufferType.EDITABLE);
		}

		// todo serso: think where to move it (keep in mind org.solovyev.android.view.AutoResizeTextView.resetTextSize())
		setAddEllipsis(false);
		setMinTextSize(10);
		resizeText();
	}

	@Override
	public void setGenericResult(@Nullable Generic genericResult) {
		this.genericResult = genericResult;
	}

	@Override
	@Nullable
	public Generic getGenericResult() {
		return genericResult;
	}

	@Override
	public int getSelection() {
		return this.getSelectionStart();
	}

	@Override
	public void setSelection(int selection) {
		// not supported by TextView
	}
}
