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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import jscl.math.function.IConstant;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.*;
import org.solovyev.android.calculator.entities.Category;
import org.solovyev.android.calculator.entities.EntityRemovalDialog;
import org.solovyev.android.calculator.math.MathType;
import org.solovyev.android.calculator.entities.BaseEntitiesFragment;
import org.solovyev.common.JPredicate;
import org.solovyev.common.collections.Collections;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class VariablesFragment extends BaseEntitiesFragment<IConstant> {

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
            return false;
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
        fab.setVisibility(View.VISIBLE);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditVariableFragment.showDialog(null, getFragmentManager());
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        bus.unregister(this);
        super.onDestroyView();
    }

    @Override
    protected void onClick(@NonNull IConstant constant) {
        keyboard.buttonPressed(constant.getName());
        final FragmentActivity activity = getActivity();
        if (activity instanceof VariablesActivity) {
            activity.finish();
        }
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
    protected boolean onMenuItemClicked(@Nonnull MenuItem item, @Nonnull final IConstant constant) {
        FragmentActivity activity = getActivity();
        switch (item.getItemId()) {
            case R.string.c_use:
                onClick(constant);
                return true;
            case R.string.c_edit:
                EditVariableFragment.showDialog(CppVariable.builder(constant).build(), activity);
                return true;
            case R.string.c_remove:
                EntityRemovalDialog.showForVariable(getActivity(), constant.getName(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Check.isTrue(which == DialogInterface.BUTTON_POSITIVE);
                        registry.remove(constant);
                    }
                });
                return true;
            case R.string.c_copy_value:
                copyText(constant.getValue());
                return true;
        }
        return false;
    }

    @Subscribe
    public void onVariableRemoved(@NonNull VariablesRegistry.RemovedEvent e) {
        onEntityRemoved(e.variable);
    }

    @Subscribe
    public void onVariableAdded(@NonNull VariablesRegistry.AddedEvent e) {
        onEntityAdded(e.variable);
    }

    @Subscribe
    public void onVariableChanged(@NonNull VariablesRegistry.ChangedEvent e) {
        onEntityChanged(e.newVariable);
    }

    @Nullable
    @Override
    protected String getDescription(@NonNull IConstant constant) {
        return registry.getDescription(constant.getName());
    }

}
