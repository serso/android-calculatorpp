/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.ParseException;
import org.solovyev.android.calculator.model.TextProcessor;

/**
 * User: serso
 * Date: 10/12/11
 * Time: 9:47 PM
 */
public class TextHighlighter implements TextProcessor<String> {

	private final int color;
	private final int colorRed;
	private final int colorGreen;
	private final int colorBlue;

	public TextHighlighter(int baseColor) {
		this.color = baseColor;
		//this.colorRed = Color.red(baseColor);
		this.colorRed = (baseColor >> 16) & 0xFF;
		//this.colorGreen = Color.green(baseColor);
		this.colorGreen = (color >> 8) & 0xFF;
		//this.colorBlue = Color.blue(baseColor);
		this.colorBlue = color & 0xFF;
	}

	@NotNull
	@Override
	public String process(@NotNull String text) throws ParseException {
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

			String s = text1.toString();
			int i = processBracketGroup(text2, s, 0, 0, maxNumberOfOpenGroupSymbols);
			for (; i < s.length(); i++) {
				text2.append(s.charAt(i));
			}

			//Log.d(CalculatorEditor.class.getName(), text2.toString());

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

			if (MathType.open_group_symbol.getTokens().contains(String.valueOf(ch))) {
				result.append(ch);
				result.append("</font>");
				i = processBracketGroup(result, s, i + 1, numberOfOpenings + 1, maxNumberOfGroups);
				result.append("<font color=\"").append(getColor(maxNumberOfGroups, numberOfOpenings)).append("\">");
				if (i < s.length() && MathType.close_group_symbol.getTokens().contains(String.valueOf(s.charAt(i)))) {
					result.append(s.charAt(i));
				}
			} else if (MathType.close_group_symbol.getTokens().contains(String.valueOf(ch))) {
				break;
			} else {
				result.append(ch);
			}
		}

		result.append("</font>");


		return i;
	}

	private String getColor(int totalNumberOfOpenings, int numberOfOpenings) {
		double c = 0.8;

		int offset = ((int) (255 * c)) * numberOfOpenings / (totalNumberOfOpenings + 1);

		// for tests:
		// innt result = Color.rgb(BASE_COLOUR_RED_COMPONENT - offset, BASE_COLOUR_GREEN_COMPONENT - offset, BASE_COLOUR_BLUE_COMPONENT - offset);
		int result = (0xFF << 24) | ((colorRed - offset) << 16) | ((colorGreen - offset) << 8) | (colorBlue - offset);

		return "#" + Integer.toHexString(result).substring(2);
	}
}
