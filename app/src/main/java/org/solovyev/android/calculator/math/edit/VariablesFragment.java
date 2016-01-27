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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;
import com.squareup.otto.Bus;

import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.CalculatorEventData;
import org.solovyev.android.calculator.CalculatorEventListener;
import org.solovyev.android.calculator.CalculatorEventType;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.Category;
import org.solovyev.android.calculator.Change;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.Locator;
import org.solovyev.android.calculator.PreparedExpression;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.ToJsclTextProcessor;
import org.solovyev.android.calculator.VariablesRegistry;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.common.JPredicate;
import org.solovyev.common.collections.Collections;
import org.solovyev.common.text.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import jscl.math.function.IConstant;

public class VariablesFragment extends BaseEntitiesFragment<IConstant> implements CalculatorEventListener {

    public static final String CREATE_VAR_EXTRA_STRING = "create_var";
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            final String varValue = bundle.getString(CREATE_VAR_EXTRA_STRING);
            if (!Strings.isEmpty(varValue)) {
                VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromValue(varValue), getFragmentManager());

                // in order to stop intent for other tabs
                bundle.remove(CREATE_VAR_EXTRA_STRING);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newInstance(), getFragmentManager());
            }
        });
    }

    @Override
    protected void onClick(@NonNull IConstant constant) {
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_constant, constant);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void addVarButtonClickHandler(@Nonnull View v) {
        VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newInstance(), this.getActivity().getSupportFragmentManager());
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

        if (!Strings.isEmpty(registry.getDescription(constant.getName()))) {
            addMenu(menu, R.string.c_copy_description, listener);
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
                VarEditDialogFragment.showDialog(VarEditDialogFragment.Input.newFromConstant(constant), activity.getSupportFragmentManager());
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
            case R.string.c_copy_description:
                final String description = registry.getDescription(constant.getName());
                if (!Strings.isEmpty(description)) {
                    Locator.getInstance().getClipboard().setText(description);
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
