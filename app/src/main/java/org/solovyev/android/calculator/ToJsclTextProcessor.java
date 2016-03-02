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

import com.google.common.collect.Iterables;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.common.msg.MessageType;
import org.solovyev.common.search.StartsWithFinder;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ToJsclTextProcessor implements TextProcessor<PreparedExpression, String> {

    @Nonnull
    private static final Integer MAX_DEPTH = 20;
    
    @Inject
    Engine engine;

    @Inject
    public ToJsclTextProcessor() {
    }

    private static PreparedExpression processWithDepth(@Nonnull String s, int depth, @Nonnull List<IConstant> undefinedVars, @Nonnull Engine engine) throws ParseException {
        return replaceVariables(processExpression(s, engine).toString(), depth, undefinedVars, engine);
    }

    @Nonnull
    private static StringBuilder processExpression(@Nonnull String s, @Nonnull Engine engine) throws ParseException {
        final StringBuilder result = new StringBuilder();
        final MathType.Results results = new MathType.Results();

        MathType.Result mathTypeResult = null;
        MathType.Result mathTypeBefore = null;

        final LiteNumberBuilder nb = new LiteNumberBuilder(engine);
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') continue;

            results.release(mathTypeBefore);
            mathTypeBefore = mathTypeResult == null ? null : mathTypeResult;
            mathTypeResult = MathType.getType(s, i, nb.isHexMode(), engine);

            nb.process(mathTypeResult);

            if (mathTypeBefore != null) {

                final MathType current = mathTypeResult.type;

                if (current.isNeedMultiplicationSignBefore(mathTypeBefore.type)) {
                    result.append("*");
                }
            }

            if (mathTypeBefore != null &&
                    (mathTypeBefore.type == MathType.function || mathTypeBefore.type == MathType.operator) &&
                    App.find(MathType.groupSymbols, s, i) != null) {
                final String functionName = mathTypeBefore.match;
                final Function function = engine.getFunctionsRegistry().get(functionName);
                if (function == null || function.getMinParameters() > 0) {
                    throw new ParseException(i, s, new CalculatorMessage(CalculatorMessages.msg_005, MessageType.error, mathTypeBefore.match));
                }
            }

            i = mathTypeResult.processToJscl(result, i);
        }
        return result;
    }

    @Nonnull
    private static PreparedExpression replaceVariables(@Nonnull final String s, int depth, @Nonnull List<IConstant> undefinedVars, @Nonnull Engine engine) throws ParseException {
        if (depth >= MAX_DEPTH) {
            throw new ParseException(s, new CalculatorMessage(CalculatorMessages.msg_006, MessageType.error));
        } else {
            depth++;
        }

        final StartsWithFinder startsWithFinder = StartsWithFinder.newInstance(s);

        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            startsWithFinder.setI(i);

            int offset = 0;
            String functionName = Iterables.find(MathType.function.getTokens(engine), startsWithFinder, null);
            if (functionName == null) {
                String operatorName = Iterables.find(MathType.operator.getTokens(engine), startsWithFinder, null);
                if (operatorName == null) {
                    String varName = Iterables.find(engine.getVariablesRegistry().getNames(), startsWithFinder, null);
                    if (varName != null) {
                        final IConstant var = engine.getVariablesRegistry().get(varName);
                        if (var != null) {
                            if (!var.isDefined()) {
                                undefinedVars.add(var);
                                result.append(varName);
                                offset = varName.length();
                            } else {
                                final String value = var.getValue();
                                if (value == null) throw new AssertionError();

                                if (var.getDoubleValue() != null) {
                                    //result.append(value);
                                    // NOTE: append varName as JSCL engine will convert it to double if needed
                                    result.append(varName);
                                } else {
                                    result.append("(").append(processWithDepth(value, depth, undefinedVars, engine)).append(")");
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

    @Override
    @Nonnull
    public PreparedExpression process(@Nonnull String s) throws ParseException {
        return processWithDepth(s, 0, new ArrayList<IConstant>(), engine);
    }
}
