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

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.model.MathEntityBuilder;
import org.solovyev.android.calculator.model.OldFunctions;
import org.solovyev.android.calculator.model.OldVar;
import org.solovyev.android.calculator.model.OldVars;
import org.solovyev.android.calculator.variables.CppVariable;
import org.solovyev.android.io.FileSaver;
import org.solovyev.common.JBuilder;
import org.solovyev.common.math.MathEntity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import jscl.JsclMathEngine;
import jscl.math.function.IConstant;

import static android.text.TextUtils.isEmpty;

@Singleton
public class VariablesRegistry extends BaseEntitiesRegistry<IConstant, OldVar> {

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
        super(mathEngine.getConstantsRegistry(), "c_var_description_", null);
    }

    public static <T extends MathEntity> void saveVariable(@Nonnull EntitiesRegistry<T> registry,
                                                           @Nonnull MathEntityBuilder<? extends T> builder,
                                                           @Nullable T editedInstance,
                                                           @Nonnull Object source, boolean save) {
        final T addedVar = registry.add(builder);

        if (save) {
            registry.save();
        }

        if (editedInstance == null) {
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.constant_added, addedVar, source);
        } else {
            Locator.getInstance().getCalculator().fireCalculatorEvent(CalculatorEventType.constant_changed, ChangeImpl.newInstance(editedInstance, addedVar), source);
        }
    }

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    public void init() {
        Check.isNotMainThread();

        try {
            //migrateOldVariables();

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
        if (isEmpty(xml)) {
            return;
        }
        try {
            final Serializer serializer = new Persister();
            final OldVars oldVariables = serializer.read(OldVars.class, xml);
            if (oldVariables != null) {
                List<CppVariable> variables = OldVars.toCppVariables(oldVariables);
                FileSaver.save(getVariablesFile(), Json.toJson(variables).toString());
            }
            preferences.edit().remove(OldFunctions.PREFS_KEY).apply();
        } catch (Exception e) {
            errorReporter.onException(e);
        }
    }

    @NonNull
    private File getVariablesFile() {
        return new File(application.getFilesDir(), "variables.json");
    }

    @Nonnull
    @Override
    protected JBuilder<? extends IConstant> createBuilder(@Nonnull OldVar entity) {
        return new OldVar.Builder(entity);
    }

    @Nonnull
    @Override
    protected PersistedEntitiesContainer<OldVar> createPersistenceContainer() {
        return new OldVars();
    }

    private void addSafely(@Nonnull String name) {
        if (!contains(name)) {
            addSafely(new OldVar.Builder(name, (String) null));
        }
    }

    @Nonnull
    @Override
    protected OldVar transform(@Nonnull IConstant entity) {
        if (entity instanceof OldVar) {
            return (OldVar) entity;
        } else {
            return new OldVar.Builder(entity).create();
        }
    }

    @Override
    public String getDescription(@Nonnull String name) {
        final IConstant var = get(name);
        if (var != null && !var.isSystem()) {
            return var.getDescription();
        } else {
            return super.getDescription(name);
        }
    }

    @Override
    public Category getCategory(@Nonnull IConstant var) {
        for (VarCategory category : VarCategory.values()) {
            if (category.isInCategory(var)) {
                return category;
            }
        }

        return null;
    }
}
