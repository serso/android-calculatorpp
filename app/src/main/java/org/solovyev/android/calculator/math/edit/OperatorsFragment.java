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

package org.solovyev.android.calculator.math.edit;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;

import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.EntitiesRegistry;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.R;
import org.solovyev.common.text.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import jscl.math.operator.Operator;

public class OperatorsFragment extends BaseEntitiesFragment<Operator> {

    @NonNull
    private final EntitiesRegistry<Operator> operatorsRegistry = Locator.getInstance().getEngine().getOperatorsRegistry();
    @NonNull
    private final EntitiesRegistry<Operator> postfixFunctionsRegistry = Locator.getInstance().getEngine().getPostfixFunctionsRegistry();

    public OperatorsFragment() {
        super(CalculatorFragmentType.operators);
    }

    @Override
    protected void onClick(@NonNull Operator operator) {
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_operator, operator);
    }

    @Nonnull
    @Override
    protected List<Operator> getEntities() {
        final List<Operator> result = new ArrayList<Operator>();

        result.addAll(operatorsRegistry.getEntities());
        result.addAll(postfixFunctionsRegistry.getEntities());

        return result;
    }

    @Override
    protected String getCategory(@Nonnull Operator operator) {
        final String result = operatorsRegistry.getCategory(operator);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }
        return postfixFunctionsRegistry.getCategory(operator);
    }

    @Override
    protected void onCreateContextMenu(@Nonnull ContextMenu menu, @Nonnull Operator operator, @Nonnull MenuItem.OnMenuItemClickListener listener) {
        addMenu(menu, R.string.c_use, listener);
        if (!Strings.isEmpty(getDescription(operator))) {
            addMenu(menu, R.string.c_copy_description, listener);
        }
    }

    @Override
    protected boolean onMenuItemClicked(@Nonnull MenuItem item, @Nonnull Operator operator) {
        switch (item.getItemId()) {
            case R.string.c_use:
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_operator, operator);
                return true;
            case R.string.c_copy_description:
                copyDescription(operator);
                return true;
        }

        return false;
    }

    @Nullable
    @Override
    protected String getDescription(@NonNull Operator operator) {
        final String name = operator.getName();
        final String result = operatorsRegistry.getDescription(name);
        if (!Strings.isEmpty(result)) {
            return result;
        }
        return postfixFunctionsRegistry.getDescription(name);
    }
}

