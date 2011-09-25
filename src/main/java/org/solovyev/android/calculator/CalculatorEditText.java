/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.math.MathEntityType;

/**
 * User: serso
 * Date: 9/17/11
 * Time: 12:25 AM
 */
public class CalculatorEditText extends EditText {


	public CalculatorEditText(Context context) {
		super(context);
	}

	public CalculatorEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalculatorEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setTextColor(ColorStateList colors) {
		super.setTextColor(colors);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public void redraw() {
		String text = getText().toString();
		int selectionStart = getSelectionStart();
		int selectionEnd = getSelectionEnd();

		int maxNumberOfOpenGroupSymbols = 0;
		int numberOfOpenGroupSymbols = 0;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (MathEntityType.openGroupSymbols.contains(ch)) {
				numberOfOpenGroupSymbols++;
				maxNumberOfOpenGroupSymbols = Math.max(maxNumberOfOpenGroupSymbols, numberOfOpenGroupSymbols);
			} else if (MathEntityType.closeGroupSymbols.contains(ch)) {
				numberOfOpenGroupSymbols--;
			}
		}

		if (maxNumberOfOpenGroupSymbols > 0) {

			final StringBuilder sb = new StringBuilder();

			processGroup(sb, text, 0, 0, maxNumberOfOpenGroupSymbols);

			Log.d(CalculatorEditText.class.getName(), sb.toString());

			super.setText(Html.fromHtml(sb.toString()), BufferType.EDITABLE);
		} else {
			super.setText(text, BufferType.EDITABLE);
		}

		setSelection(selectionStart, selectionEnd);
	}

	private int processGroup(@NotNull StringBuilder result, @NotNull String s, int i, int numberOfOpenings, int maxNumberOfGroups) {

		result.append("<font color=\"").append(getColor(maxNumberOfGroups, numberOfOpenings)).append("\">");

		for (; i < s.length(); i++) {
			char ch = s.charAt(i);

			if (MathEntityType.openGroupSymbols.contains(ch)) {
				result.append(ch);
				result.append("</font>");
				i = processGroup(result, s, i + 1, numberOfOpenings + 1, maxNumberOfGroups);
				result.append("<font color=\"").append(getColor(maxNumberOfGroups, numberOfOpenings)).append("\">");
				if (i < s.length() && MathEntityType.closeGroupSymbols.contains(s.charAt(i))) {
					result.append(s.charAt(i));
				}
			} else if (MathEntityType.closeGroupSymbols.contains(ch)) {
				break;
			} else {
				result.append(ch);
			}
		}

		result.append("</font>");


		return i;
	}

	private String getColor(int numberOfOpenGroupSymbols, int numberOfOpenings) {
		final int baseColor = Color.WHITE;

		double c = 1;

		int i = ((int)(255 * c)) * numberOfOpenings / (numberOfOpenGroupSymbols + 1);

		int result = Color.rgb( Color.red(baseColor) - i, Color.green(baseColor) - i, Color.blue(baseColor) - i);

		return "#" + Integer.toHexString(result).substring(2);
	}
}
