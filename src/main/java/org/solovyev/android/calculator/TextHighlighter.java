/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.MathContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.model.*;
import org.solovyev.common.utils.MutableObject;

import java.util.HashMap;
import java.util.Map;

/**
 * User: serso
 * Date: 10/12/11
 * Time: 9:47 PM
 */
public class TextHighlighter implements TextProcessor<TextHighlighter.Result, String> {

	private static final Map<String, String> nbFontAttributes = new HashMap<String, String>();

	static {
		nbFontAttributes.put("color", "#008000");
	}

	@NotNull
	public final MathContext mathContext;

	public static class Result implements CharSequence {

		@NotNull
		private final String string;

		private final int offset;

		public Result(@NotNull String string, int offset) {
			this.string = string;
			this.offset = offset;
		}

		@Override
		public int length() {
			return string.length();
		}

		@Override
		public char charAt(int i) {
			return string.charAt(i);
		}

		@Override
		public CharSequence subSequence(int i, int i1) {
			return string.subSequence(i, i1);
		}

		@Override
		public String toString() {
			return string;
		}

		public int getOffset() {
			return offset;
		}
	}

	private final int color;
	private final int colorRed;
	private final int colorGreen;
	private final int colorBlue;
	private final boolean formatNumber;

	public TextHighlighter(int baseColor, boolean formatNumber, @NotNull MathContext mathContext) {
		this.color = baseColor;
		this.formatNumber = formatNumber;
		this.mathContext = mathContext;
		//this.colorRed = Color.red(baseColor);
		this.colorRed = (baseColor >> 16) & 0xFF;
		//this.colorGreen = Color.green(baseColor);
		this.colorGreen = (color >> 8) & 0xFF;
		//this.colorBlue = Color.blue(baseColor);
		this.colorBlue = color & 0xFF;
	}

	@NotNull
	@Override
	public Result process(@NotNull String text) throws CalculatorParseException {
		final String result;

		int maxNumberOfOpenGroupSymbols = 0;
		int numberOfOpenGroupSymbols = 0;

		final StringBuilder text1 = new StringBuilder();

		int resultOffset = 0;

		final AbstractNumberBuilder numberBuilder;
		if (!formatNumber) {
			numberBuilder = new LiteNumberBuilder(CalculatorEngine.instance.getEngine());
		} else {
			numberBuilder = new NumberBuilder(CalculatorEngine.instance.getEngine());
		}
		for (int i = 0; i < text.length(); i++) {
			MathType.Result mathType = MathType.getType(text, i, numberBuilder.isHexMode());

			if (numberBuilder instanceof NumberBuilder) {
				final MutableObject<Integer> numberOffset = new MutableObject<Integer>(0);
				((NumberBuilder) numberBuilder).process(text1, mathType, numberOffset);
				resultOffset += numberOffset.getObject();
			} else {
				((LiteNumberBuilder) numberBuilder).process(mathType);
			}

			final String match = mathType.getMatch();
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
				case operator:
					text1.append(match);
					if (match.length() > 1) {
						i += match.length() - 1;
					}
					break;
				case function:
					i = processHighlightedText(text1, i, match, "i", null);
					break;
				case constant:
					i = processHighlightedText(text1, i, match, "b", null);
					break;
				case numeral_base:
					i = processHighlightedText(text1, i, match, "b", null);
					break;
				default:
					if (mathType.getMathType() == MathType.text || match.length() <= 1) {
						text1.append(text.charAt(i));
					} else {
						text1.append(match);
						i += match.length() - 1;
					}
			}
		}

		if (numberBuilder instanceof NumberBuilder) {
			final MutableObject<Integer> numberOffset = new MutableObject<Integer>(0);
			((NumberBuilder) numberBuilder).processNumber(text1, numberOffset);
			resultOffset += numberOffset.getObject();
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

		return new Result(result, resultOffset);
	}

	private int processHighlightedText(@NotNull StringBuilder result, int i, @NotNull String match, @NotNull String tag, @Nullable Map<String, String> tagAttributes) {
		result.append("<").append(tag);

		if (tagAttributes != null) {
			for (Map.Entry<String, String> entry : tagAttributes.entrySet()) {
				// attr1="attr1_value" attr2="attr2_value"
				result.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
			}
		}

		result.append(">").append(match).append("</").append(tag).append(">");
		if (match.length() > 1) {
			return i + match.length() - 1;
		} else {
			return i;
		}
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
