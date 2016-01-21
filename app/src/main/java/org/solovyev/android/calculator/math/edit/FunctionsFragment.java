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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.solovyev.android.calculator.AppComponent;
import org.solovyev.android.calculator.Calculator;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.EntitiesRegistry;
import org.solovyev.android.calculator.FunctionsRegistry;
import org.solovyev.android.calculator.Keyboard;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.function.CppFunction;
import org.solovyev.android.calculator.function.EditFunctionFragment;
import org.solovyev.common.text.Strings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import jscl.math.function.Function;
import jscl.math.function.IFunction;

public class FunctionsFragment extends BaseEntitiesFragment<Function> {

    public static final String EXTRA_FUNCTION = "function";
    @Inject
    FunctionsRegistry registry;
    @Inject
    Calculator calculator;
    @Inject
    Keyboard keyboard;
    @Inject
    Bus bus;

    public FunctionsFragment() {
        super(CalculatorFragmentType.functions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle = getArguments();
        if (bundle != null) {
            final CppFunction function = bundle.getParcelable(EXTRA_FUNCTION);
            if (function != null) {
                EditFunctionFragment.showDialog(function, getFragmentManager());
                // in order to stop intent for other tabs
                bundle.remove(EXTRA_FUNCTION);
            }
        }
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        bus.register(this);
        return view;
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        fab.setVisibility(View.VISIBLE);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFunctionFragment.showDialog(getActivity());
            }
        });
    }

    @Override
    protected void onClick(@Nonnull Function function) {
        keyboard.buttonPressed(function.getName());
        final FragmentActivity activity = getActivity();
        if (activity instanceof CalculatorFunctionsActivity) {
            activity.finish();
        }
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
                onClick(function);
                return true;
            case R.string.c_edit:
                if (function instanceof IFunction) {
                    EditFunctionFragment.showDialog(CppFunction.builder((IFunction) function).build(), activity.getSupportFragmentManager());
                }
                return true;
            case R.string.c_remove:
                // todo serso:
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
    public void onDestroyView() {
        bus.unregister(this);
        super.onDestroyView();
    }

    @Subscribe
    public void onFunctionAdded(@NonNull final FunctionsRegistry.AddedEvent event) {
        if (!isInCategory(event.function)) {
            return;
        }
        final EntitiesAdapter adapter = getAdapter();
        adapter.add(event.function);
        adapter.sort();
    }

    @Subscribe
    public void onFunctionChanged(@NonNull final FunctionsRegistry.ChangedEvent event) {
        if (!isInCategory(event.newFunction)) {
            return;
        }
        if (!event.oldFunction.isIdDefined()) {
            return;
        }
        final EntitiesAdapter adapter = getAdapter();
        if (adapter == null) {
            return;
        }
        for (int i = 0; i < adapter.getItemCount(); i++) {
            final Function adapterFunction = adapter.getItem(i);
            if (adapterFunction.isIdDefined() && event.oldFunction.getId().equals(adapterFunction.getId())) {
                adapter.set(i, adapterFunction);
                break;
            }
        }
        adapter.sort();
    }

    @Subscribe
    public void onFunctionRemoved(@NonNull final FunctionsRegistry.RemovedEvent event) {
        final EntitiesAdapter adapter = getAdapter();
        adapter.remove(event.function);
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    protected String getDescription(@NonNull Function function) {
        return registry.getDescription(function.getName());
    }
}
