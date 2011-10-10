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
import android.view.ContextMenu;
import android.widget.EditText;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.math.MathType;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 12:25 AM
 */
public class CalculatorEditor extends EditText {

	private boolean highlightText = true;

	private final static int BASE_COLOUR = Color.WHITE;
	private final static int BASE_COLOUR_RED_COMPONENT = Color.red(BASE_COLOUR);
	private final static int BASE_COLOUR_GREEN_COMPONENT = Color.green(BASE_COLOUR);
	private final static int BASE_COLOUR_BLUE_COMPONENT = Color.blue(BASE_COLOUR);

	public CalculatorEditor(Context context) {
		super(context);
	}

	public CalculatorEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalculatorEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onCheckIsTextEditor() {
		return false;
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		super.onCreateContextMenu(menu);

		menu.removeItem(android.R.id.selectAll);
		menu.removeItem(android.R.id.startSelectingText);
	}



	public void redraw() {
		String text = getText().toString();

		int selectionStart = getSelectionStart();
		int selectionEnd = getSelectionEnd();

		if (highlightText) {

			text = highlightText(text);

			super.setText(Html.fromHtml(text), BufferType.EDITABLE);
		} else {
			super.setText(text, BufferType.EDITABLE);
		}

		setSelection(selectionStart, selectionEnd);
	}

	private String highlightText(@NotNull final String text) {
		final String result;

		int maxNumberOfOpenGroupSymbols = 0;
		int numberOfOpenGroupSymbols = 0;

		final StringBuilder text1 = new StringBuilder();

		for (int i = 0; i < text.length(); i++) {
			final MathType.Result mathType = MathType.getType(text, i);

			switch (mathType.getMathType()) {
				case open_group_symbol:
					numberOfOpenGroupSymbols++;
					maxNumberOfOpenGroupSymbols = Math.max(maxNumberOfOpenGroupSymbols, numberOfOpenGroupSymbols);
					text1.append(text.charAt(i));
					break;
				case close_group_symbol:
					numberOfOpenGroupSymbols--;
					text1.append(text.charAt(i));
					break;
				case function:
					i = processHighlightedText(text1, i, mathType.getMatch(), "i");
					break;
				case constant:
					i = processHighlightedText(text1, i, mathType.getMatch(), "b");
					break;
				default:
					text1.append(text.charAt(i));
			}
		}

		if (maxNumberOfOpenGroupSymbols > 0) {

			final StringBuilder text2 = new StringBuilder();

			processBracketGroup(text2, text1.toString(), 0, 0, maxNumberOfOpenGroupSymbols);

			Log.d(CalculatorEditor.class.getName(), text2.toString());

			result = text2.toString();
		} else {
			result = text1.toString();
		}

		return result;
	}

	private int processHighlightedText(@NotNull StringBuilder result, int i, @NotNull String functionName, @NotNull String tag) {
		result.append("<").append(tag).append(">").append(functionName).append("</").append(tag).append(">");
		return i + functionName.length() - 1;
	}

	private int processBracketGroup(@NotNull StringBuilder result, @NotNull String s, int i, int numberOfOpenings, int maxNumberOfGroups) {

		result.append("<font color=\"").append(getColor(maxNumberOfGroups, numberOfOpenings)).append("\">");

		for (; i < s.length(); i++) {
			char ch = s.charAt(i);

			if (MathType.openGroupSymbols.contains(ch)) {
				result.append(ch);
				result.append("</font>");
				i = processBracketGroup(result, s, i + 1, numberOfOpenings + 1, maxNumberOfGroups);
				result.append("<font color=\"").append(getColor(maxNumberOfGroups, numberOfOpenings)).append("\">");
				if (i < s.length() && MathType.closeGroupSymbols.contains(s.charAt(i))) {
					result.append(s.charAt(i));
				}
			} else if (MathType.closeGroupSymbols.contains(ch)) {
				break;
			} else {
				result.append(ch);
			}
		}

		result.append("</font>");


		return i;
	}

	private String getColor(int numberOfOpenGroupSymbols, int numberOfOpenings) {
		double c = 0.7;

		int offset = ((int) (255 * c)) * numberOfOpenings / (numberOfOpenGroupSymbols + 1);

		int result = Color.rgb(BASE_COLOUR_RED_COMPONENT - offset, BASE_COLOUR_GREEN_COMPONENT - offset, BASE_COLOUR_BLUE_COMPONENT - offset);

		return "#" + Integer.toHexString(result).substring(2);
	}

	public boolean isHighlightText() {
		return highlightText;
	}

	public void setHighlightText(boolean highlightText) {
		this.highlightText = highlightText;
		redraw();
	}
}
