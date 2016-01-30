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
import jscl.JsclMathEngine;
import jscl.math.function.IConstant;
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
import org.solovyev.common.JBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class VariablesRegistry extends BaseEntitiesRegistry<IConstant> {

    @Nonnull
    public static final String ANS = "ans";

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<String, String>();

    static {
        substitutes.put("π", "pi");
        substitutes.put("Π", "PI");
        substitutes.put("∞", "inf");
        substitutes.put("h", "h_reduced");
        substitutes.put("NaN", "nan");
    }

    @Inject
    public VariablesRegistry(@Nonnull JsclMathEngine mathEngine) {
        super(mathEngine.getConstantsRegistry(), "c_var_description_");
    }

    public void add(@NonNull JBuilder<? extends IConstant> builder, @Nullable IConstant oldVariable) {
        final IConstant variable = add(builder);
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

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    public void init() {
        Check.isNotMainThread();

        try {
            migrateOldVariables();

            for (CppVariable variable : loadEntities(CppVariable.JSON_CREATOR)) {
                addSafely(variable.toJsclBuilder());
            }

            addSafely("x");
            addSafely("y");
            addSafely("t");
            addSafely("j");
        } finally {
            setInitialized();
        }
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
        return new File(application.getFilesDir(), "variables.json");
    }

    @Nullable
    @Override
    protected Jsonable toJsonable(@NonNull IConstant constant) {
        return CppVariable.builder(constant).build();
    }

    private void addSafely(@Nonnull String name) {
        if (!contains(name)) {
            addSafely(CppVariable.builder(name).build().toJsclBuilder());
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
