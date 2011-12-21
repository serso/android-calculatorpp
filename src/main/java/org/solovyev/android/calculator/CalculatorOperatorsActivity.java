package org.solovyev.android.calculator;

import android.content.Context;
import jscl.math.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;
import org.solovyev.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 11/17/11
 * Time: 1:53 PM
 */

public class CalculatorOperatorsActivity extends AbstractMathEntityListActivity<Operator> {

    @NotNull
    @Override
    protected MathEntityDescriptionGetter getDescriptionGetter() {
        return new OperatorDescriptionGetter();
    }


    @NotNull
    @Override
    protected List<Operator> getMathEntities() {
        final List<Operator> result = new ArrayList<Operator>();

        result.addAll(CalculatorEngine.instance.getOperatorsRegistry().getEntities());
        result.addAll(CalculatorEngine.instance.getPostfixFunctionsRegistry().getEntities());

        return result;
    }

    @Override
    protected String getMathEntityCategory(@NotNull Operator operator) {
        String result = CalculatorEngine.instance.getOperatorsRegistry().getCategory(operator);
        if (result == null) {
            result = CalculatorEngine.instance.getPostfixFunctionsRegistry().getCategory(operator);
        }

        return result;
    }

    private static class OperatorDescriptionGetter implements MathEntityDescriptionGetter {

        @Override
        public String getDescription(@NotNull Context context, @NotNull String mathEntityName) {
            String result = CalculatorEngine.instance.getOperatorsRegistry().getDescription(context, mathEntityName);
            if (StringUtils.isEmpty(result)) {
                result = CalculatorEngine.instance.getPostfixFunctionsRegistry().getDescription(context, mathEntityName);
            }

            return result;
        }
    }

}

