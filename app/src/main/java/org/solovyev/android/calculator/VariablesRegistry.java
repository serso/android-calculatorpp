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

import android.support.annotation.NonNull;

import com.google.common.base.Strings;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.entities.BaseEntitiesRegistry;
import org.solovyev.android.calculator.entities.Category;
import org.solovyev.android.calculator.entities.Entities;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;
import org.solovyev.android.calculator.variables.CppVariable;
import org.solovyev.android.calculator.variables.OldVars;
import org.solovyev.android.calculator.variables.VariableCategory;
import org.solovyev.android.io.FileSaver;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import jscl.JsclMathEngine;
import jscl.math.function.IConstant;

@Singleton
public class VariablesRegistry extends BaseEntitiesRegistry<IConstant> {
    {
        addDescription("Π", R.string.c_var_description_PI);
        addDescription("π", R.string.c_var_description_pi);
        addDescription("e", R.string.c_var_description_e);
        addDescription("i", R.string.c_var_description_i);
        addDescription("c", R.string.c_var_description_c);
        addDescription("G", R.string.c_var_description_G);
        addDescription("h", R.string.c_var_description_h_reduced);
        addDescription("∞", R.string.c_var_description_inf);
        addDescription("inf", R.string.c_var_description_inf);
        addDescription("nan", R.string.c_var_description_nan);
        addDescription("NaN", R.string.c_var_description_nan);
    }

    @Inject
    public VariablesRegistry(@Nonnull JsclMathEngine mathEngine) {
        super(mathEngine.getConstantsRegistry());
    }

    public void addOrUpdate(@Nonnull IConstant newVariable, @Nullable IConstant oldVariable) {
        final IConstant variable = addOrUpdate(newVariable);
        if (oldVariable == null) {
            bus.post(new AddedEvent(variable));
        } else {
            bus.post(new ChangedEvent(oldVariable, variable));
        }
    }

    @Override
    public void remove(@Nonnull IConstant variable) {
        super.remove(variable);
        bus.post(new RemovedEvent(variable));
    }

    @Override
    protected void onInit() {
        Check.isNotMainThread();

        migrateOldVariables();

        for (CppVariable variable : loadEntities(CppVariable.JSON_CREATOR)) {
            addSafely(variable.toJsclConstant());
        }

        addSafely("x");
        addSafely("y");
        addSafely("t");
        addSafely("j");
    }

    private void migrateOldVariables() {
        final String xml = preferences.getString(OldVars.PREFS_KEY, null);
        if (Strings.isNullOrEmpty(xml)) {
            return;
        }
        try {
            final Serializer serializer = new Persister();
            final OldVars oldVariables = serializer.read(OldVars.class, xml);
            if (oldVariables != null) {
                List<CppVariable> variables = OldVars.toCppVariables(oldVariables);
                FileSaver.save(getEntitiesFile(), Json.toJson(variables).toString());
            }
            preferences.edit().remove(OldVars.PREFS_KEY).apply();
        } catch (Exception e) {
            errorReporter.onException(e);
        }
    }

    @Override
    @NonNull
    protected File getEntitiesFile() {
        return new File(filesDir.get(), "variables.json");
    }

    @Nullable
    @Override
    protected Jsonable toJsonable(@NonNull IConstant constant) {
        return CppVariable.builder(constant).build();
    }

    private void addSafely(@Nonnull String name) {
        if (!contains(name)) {
            addSafely(CppVariable.builder(name).build().toJsclConstant());
        }
    }

    @Override
    public String getDescription(@Nonnull String name) {
        final IConstant variable = get(name);
        if (variable != null && !variable.isSystem()) {
            return variable.getDescription();
        } else {
            return super.getDescription(name);
        }
    }

    @Override
    public Category getCategory(@Nonnull IConstant variable) {
        return Entities.getCategory(variable, VariableCategory.values());
    }

    public static final class AddedEvent {
        @NonNull
        public final IConstant variable;

        public AddedEvent(@NonNull IConstant variable) {
            this.variable = variable;
        }
    }

    public static final class ChangedEvent {
        @NonNull
        public final IConstant oldVariable;
        @NonNull
        public final IConstant newVariable;

        public ChangedEvent(@NonNull IConstant oldVariable, @NonNull IConstant newVariable) {
            this.oldVariable = oldVariable;
            this.newVariable = newVariable;
        }
    }

    public static final class RemovedEvent {
        @NonNull
        public final IConstant variable;

        public RemovedEvent(@NonNull IConstant variable) {
            this.variable = variable;
        }
    }
}
