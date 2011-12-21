/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import jscl.math.function.Function;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.model.CalculatorEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 10/29/11
 * Time: 4:55 PM
 */
public class CalculatorFunctionsTabActivity extends AbstractMathEntityListActivity<Function> {

    @NotNull
    @Override
    protected MathEntityDescriptionGetter getDescriptionGetter() {
        return new MathEntityDescriptionGetterImpl(CalculatorEngine.instance.getFunctionsRegistry());
    }

    @NotNull
    @Override
    protected List<Function> getMathEntities() {
        return new ArrayList<Function>(CalculatorEngine.instance.getFunctionsRegistry().getEntities());
    }

    @Override
    protected String getMathEntityCategory(@NotNull Function function) {
        return CalculatorEngine.instance.getFunctionsRegistry().getCategory(function);
    }
}
