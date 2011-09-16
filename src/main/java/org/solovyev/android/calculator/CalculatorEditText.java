/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.calculator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;
import org.solovyev.common.definitions.Pair;
import org.solovyev.common.exceptions.SersoException;
import org.solovyev.common.utils.CollectionsUtils;
import org.solovyev.util.math.MathEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

	@Override
	public void setText(CharSequence text, BufferType type) {
		/*try {
			final List<Pair<Integer, Integer>> groupSymbols = new ArrayList<Pair<Integer, Integer>>();

			final Stack<Pair<Integer, Integer>> groupSymbolsStack = new Stack<Pair<Integer, Integer>>();
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				if (MathEntityType.openGroupSymbols.contains(ch)) {
					groupSymbolsStack.push(new Pair<Integer, Integer>(i, null));
				} else if (MathEntityType.closeGroupSymbols.contains(ch)) {
					final Pair<Integer, Integer> pair = groupSymbolsStack.pop();
					if (pair != null) {
						pair.setSecond(i);
						groupSymbols.add(0, pair);
					} else {
						throw new NoPairGroupSymbolException();
					}
				}
			}

			text = insertHtml(text, groupSymbols);
		} catch (NoPairGroupSymbolException e) {
			// do nothing
		}*/

		super.setText(text, type);
	}

	private class NoPairGroupSymbolException extends SersoException {

	}
}
