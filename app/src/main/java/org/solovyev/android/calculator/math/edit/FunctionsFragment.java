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
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.function.EditFunctionFragment;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FunctionsFragment extends BaseEntitiesFragment<Function> {

    public static final String EXTRA_FUNCTION = "function";
    @NonNull
    private final EntitiesRegistry<Function> registry = Locator.getInstance().getEngine().getFunctionsRegistry();

    public FunctionsFragment() {
        super(CalculatorFragmentType.functions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            final EditFunctionFragment.Input input = bundle.getParcelable(EXTRA_FUNCTION);
            if (input != null) {
                EditFunctionFragment.showDialog(input, getFragmentManager());
                // in order to stop intent for other tabs
                bundle.remove(EXTRA_FUNCTION);
            }
        }
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        fab.setVisibility(View.VISIBLE);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFunctionFragment.showDialog(EditFunctionFragment.Input.newInstance(), getFragmentManager());
            }
        });
    }

    @Override
    protected void onClick(@NonNull @Nonnull Function function) {
        Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_function, function);
    }

    @Override
    protected void onCreateContextMenu(@Nonnull ContextMenu menu, @Nonnull Function function, @NonNull MenuItem.OnMenuItemClickListener listener) {
        addMenu(menu, R.string.c_use, listener);
        final EntitiesRegistry<Function> functionsRegistry = registry;
        if (!Strings.isEmpty(functionsRegistry.getDescription(function.getName()))) {
            addMenu(menu, R.string.c_copy_description, listener);
        }

        if (!function.isSystem()) {
            addMenu(menu, R.string.c_edit, listener);
            addMenu(menu, R.string.c_remove, listener);
        }
    }

    @Override
    protected boolean onMenuItemClicked(@Nonnull MenuItem item, @Nonnull Function function) {
        final FragmentActivity activity = getActivity();
        switch (item.getItemId()) {
            case R.string.c_use:
                Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.use_function, function);
                return true;
            case R.string.c_edit:
                if (function instanceof IFunction) {
                    EditFunctionFragment.showDialog(EditFunctionFragment.Input.newFromFunction((IFunction) function), activity.getSupportFragmentManager());
                }
                return true;
            case R.string.c_remove:
                MathEntityRemover.newFunctionRemover(function, null, activity, activity).showConfirmationDialog();
                return true;
            case R.string.c_copy_description:
                copyDescription(function);
                return true;
        }
        return false;
    }

    @Nonnull
    @Override
    protected List<Function> getEntities() {
        return new ArrayList<>(registry.getEntities());
    }

    @Override
    protected String getCategory(@Nonnull Function function) {
        return registry.getCategory(function);
    }

    @Override
    public void onCalculatorEvent(@Nonnull CalculatorEventData calculatorEventData, @Nonnull CalculatorEventType calculatorEventType, @Nullable Object data) {
        super.onCalculatorEvent(calculatorEventData, calculatorEventType, data);

        switch (calculatorEventType) {
            case function_added:
                processFunctionAdded((Function) data);
                break;

            case function_changed:
                processFunctionChanged((Change<IFunction>) data);
                break;

            case function_removed:
                processFunctionRemoved((Function) data);
                break;
        }
    }

    @Nullable
    @Override
    protected String getDescription(@NonNull Function function) {
        return registry.getDescription(function.getName());
    }


    private void processFunctionRemoved(@Nonnull final Function function) {
        if (this.isInCategory(function)) {
            getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    removeFromAdapter(function);
                    notifyAdapter();
                }
            });
        }
    }

    private void processFunctionChanged(@Nonnull final Change<IFunction> change) {
        final IFunction newFunction = change.getNewValue();

        if (newFunction instanceof Function) {

            if (this.isInCategory((Function) newFunction)) {

                getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        IFunction oldValue = change.getOldValue();

                        if (oldValue.isIdDefined()) {
                            final EntitiesAdapter adapter = getAdapter();
                            if (adapter != null) {
                                for (int i = 0; i < adapter.getItemCount(); i++) {
                                    final Function functionFromAdapter = adapter.getItem(i);
                                    if (functionFromAdapter.isIdDefined() && oldValue.getId().equals(functionFromAdapter.getId())) {
                                        //adapter.remove(functionFromAdapter);
                                        break;
                                    }
                                }
                            }
                        }

                        addToAdapter((Function) newFunction);
                        sort();
                    }
                });
            }
        } else {
            throw new IllegalArgumentException("Function must be instance of jscl.math.function.Function class!");
        }
    }

    private void processFunctionAdded(@Nonnull final Function function) {
        if (this.isInCategory(function)) {
            getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    addToAdapter(function);
                    sort();
                }
            });
        }
    }
}
