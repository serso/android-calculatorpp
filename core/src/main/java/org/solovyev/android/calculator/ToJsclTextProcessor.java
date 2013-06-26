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

package org.solovyev.android.calculator;

import jscl.math.function.Function;
import jscl.math.function.IConstant;

import javax.annotation.Nonnull;

import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.common.collections.Collections;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.search.StartsWithFinder;

import java.util.ArrayList;
import java.util.List;

public class ToJsclTextProcessor implements TextProcessor<PreparedExpression, String> {

	@Nonnull
	private static final Integer MAX_DEPTH = 20;

	@Nonnull
	private static final TextProcessor<PreparedExpression, String> instance = new ToJsclTextProcessor();

	private ToJsclTextProcessor() {
	}


	@Nonnull
	public static TextProcessor<PreparedExpression, String> getInstance() {
		return instance;
	}

	@Override
	@Nonnull
	public PreparedExpression process(@Nonnull String s) throws CalculatorParseException {
		return processWithDepth(s, 0, new ArrayList<IConstant>());
	}

	private static PreparedExpression processWithDepth(@Nonnull String s, int depth, @Nonnull List<IConstant> undefinedVars) throws CalculatorParseException {
		return replaceVariables(processExpression(s).toString(), depth, undefinedVars);
	}

	@Nonnull
	private static StringBuilder processExpression(@Nonnull String s) throws CalculatorParseException {
		final StartsWithFinder startsWithFinder = StartsWithFinder.newInstance(s);
		final StringBuilder result = new StringBuilder();

		MathType.Result mathTypeResult = null;
		MathType.Result mathTypeBefore;

		final LiteNumberBuilder nb = new LiteNumberBuilder(Locator.getInstance().getEngine());
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ' ') continue;
			startsWithFinder.setI(i);

			mathTypeBefore = mathTypeResult == null ? null : mathTypeResult;

			mathTypeResult = MathType.getType(s, i, nb.isHexMode());

			nb.process(mathTypeResult);

			if (mathTypeBefore != null) {

				final MathType current = mathTypeResult.getMathType();

				if (current.isNeedMultiplicationSignBefore(mathTypeBefore.getMathType())) {
					result.append("*");
				}
			}

			if (mathTypeBefore != null &&
					(mathTypeBefore.getMathType() == MathType.function || mathTypeBefore.getMathType() == MathType.operator) &&
					Collections.find(MathType.openGroupSymbols, startsWithFinder) != null) {
				final String functionName = mathTypeBefore.getMatch();
				final Function function = Locator.getInstance().getEngine().getFunctionsRegistry().get(functionName);
				if (function == null || function.getMinParameters() > 0) {
					throw new CalculatorParseException(i, s, new CalculatorMessage(CalculatorMessages.msg_005, MessageType.error, mathTypeBefore.getMatch()));
				}
			}

			i = mathTypeResult.processToJscl(result, i);
		}
		return result;
	}

	@Nonnull
	private static PreparedExpression replaceVariables(@Nonnull final String s, int depth, @Nonnull List<IConstant> undefinedVars) throws CalculatorParseException {
		if (depth >= MAX_DEPTH) {
			throw new CalculatorParseException(s, new CalculatorMessage(CalculatorMessages.msg_006, MessageType.error));
		} else {
			depth++;
		}

		final StartsWithFinder startsWithFinder = StartsWithFinder.newInstance(s);

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			startsWithFinder.setI(i);

			int offset = 0;
			String functionName = Collections.find(MathType.function.getTokens(), startsWithFinder);
			if (functionName == null) {
				String operatorName = Collections.find(MathType.operator.getTokens(), startsWithFinder);
				if (operatorName == null) {
					String varName = Collections.find(Locator.getInstance().getEngine().getVarsRegistry().getNames(), startsWithFinder);
					if (varName != null) {
						final IConstant var = Locator.getInstance().getEngine().getVarsRegistry().get(varName);
						if (var != null) {
							if (!var.isDefined()) {
								undefinedVars.add(var);
								result.append(varName);
								offset = varName.length();
							} else {
								final String value = var.getValue();
								assert value != null;

								if (var.getDoubleValue() != null) {
									//result.append(value);
									// NOTE: append varName as JSCL engine will convert it to double if needed
									result.append(varName);
								} else {
									result.append("(").append(processWithDepth(value, depth, undefinedVars)).append(")");
								}
								offset = varName.length();
							}
						}
					}
				} else {
					result.append(operatorName);
					offset = operatorName.length();
				}
			} else {
				result.append(functionName);
				offset = functionName.length();
			}


			if (offset == 0) {
				result.append(s.charAt(i));
			} else {
				i += offset - 1;
			}
		}

		return new PreparedExpression(result.toString(), undefinedVars);
	}
}
