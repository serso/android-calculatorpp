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

package org.solovyev.android.calculator.variables;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import com.squareup.otto.Bus;
import jscl.math.function.IConstant;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.entities.Category;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.math.edit.BaseEntitiesFragment;
import org.solovyev.android.calculator.math.edit.MathEntityRemover;
import org.solovyev.common.JPredicate;
import org.solovyev.common.collections.Collections;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class VariablesFragment extends BaseEntitiesFragment<IConstant> implements CalculatorEventListener {

    @Inject
    VariablesRegistry registry;
    @Inject
    Calculator calculator;
    @Inject
    Keyboard keyboard;
    @Inject
    Bus bus;

    public VariablesFragment() {
        super(CalculatorFragmentType.variables);
    }

    public static boolean isValidValue(@Nonnull String value) {
        try {
            final PreparedExpression expression = ToJsclTextProcessor.getInstance().process(value);
            final List<IConstant> constants = expression.getUndefinedVars();
            return constants.isEmpty();
        } catch (RuntimeException e) {
            return true;
        }
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        fab.setVisibility(View.VISIBLE);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditVariableFragment.showDialog(null, getFragmentManager());
            }
        });
    }

    @Override
    protected void onClick(@NonNull IConstant constant) {
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_constant, constant);
    }

    @Nonnull
    @Override
    protected List<IConstant> getEntities() {
        final List<IConstant> result = new ArrayList<IConstant>(registry.getEntities());

        Collections.removeAll(result, new JPredicate<IConstant>() {
            @Override
            public boolean apply(@Nullable IConstant var) {
                return var != null && Collections.contains(var.getName(), MathType.INFINITY_JSCL, MathType.NAN);
            }
        });

        return result;
    }

    @Override
    protected Category getCategory(@Nonnull IConstant var) {
        return registry.getCategory(var);
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        switch (calculatorEventType) {
            case constant_added:
                processConstantAdded((IConstant) data);
                break;

            case constant_changed:
                processConstantChanged((Change<IConstant>) data);
                break;

            case constant_removed:
                processConstantRemoved((IConstant) data);
                break;
        }
    }

    @Override
    protected void onCreateContextMenu(@Nonnull ContextMenu menu, @Nonnull IConstant constant, @Nonnull MenuItem.OnMenuItemClickListener listener) {
        addMenu(menu, R.string.c_use, listener);
        if (!constant.isSystem()) {
            addMenu(menu, R.string.c_edit, listener);
            addMenu(menu, R.string.c_remove, listener);
        }

        if (!Strings.isEmpty(constant.getValue())) {
            addMenu(menu, R.string.c_copy_value, listener);
        }
    }

    @Override
    protected boolean onMenuItemClicked(@Nonnull MenuItem item, @Nonnull IConstant constant) {
        FragmentActivity activity = getActivity();
        switch (item.getItemId()) {
            case R.string.c_use:
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_constant, constant);
                return true;
            case R.string.c_edit:
                EditVariableFragment.showDialog(CppVariable.builder(constant).build(), activity);
                return true;
            case R.string.c_remove:
                MathEntityRemover.newConstantRemover(constant, null, activity, activity).showConfirmationDialog();
                return true;
            case R.string.c_copy_value:
                final String value = constant.getValue();
                if (!Strings.isEmpty(value)) {
                    Locator.getInstance().getClipboard().setText(value);
                }
                return true;
        }
        return false;
    }

    private void processConstantRemoved(@Nonnull final IConstant constant) {
        if (this.isInCategory(constant)) {
            getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    final EntitiesAdapter adapter = getAdapter();
                    adapter.remove(constant);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void processConstantChanged(@Nonnull final Change<IConstant> change) {
        final IConstant newConstant = change.getNewValue();
        if (this.isInCategory(newConstant)) {
            getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    final EntitiesAdapter adapter = getAdapter();
                    adapter.remove(change.getOldValue());
                    adapter.add(newConstant);
                    adapter.sort();
                }
            });
        }
    }

    private void processConstantAdded(@Nonnull final IConstant constant) {
        if (this.isInCategory(constant)) {
            getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    final EntitiesAdapter adapter = getAdapter();
                    adapter.add(constant);
                    adapter.sort();
                }
            });
        }
    }

    @Nullable
    @Override
    protected String getDescription(@NonNull IConstant constant) {
        return registry.getDescription(constant.getName());
    }

}
