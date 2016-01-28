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
import org.solovyev.android.calculator.entities.Category;
import org.solovyev.android.calculator.entities.Entities;
import org.solovyev.android.calculator.function.CppFunction;
import org.solovyev.android.calculator.function.FunctionCategory;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;
import org.solovyev.android.calculator.function.OldFunctions;
import org.solovyev.android.io.FileSaver;
import org.solovyev.common.JBuilder;
import org.solovyev.common.text.Strings;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import jscl.JsclMathEngine;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;

import static android.text.TextUtils.isEmpty;

@Singleton
public class FunctionsRegistry extends BaseEntitiesRegistry<Function> {

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<>();

    static {
        substitutes.put("√", "sqrt");
    }

    @Inject
    Calculator calculator;

    @Inject
    public FunctionsRegistry(@Nonnull JsclMathEngine mathEngine) {
        super(mathEngine.getFunctionsRegistry(), "c_fun_description_");
    }

    public void add(@NonNull JBuilder<? extends Function> builder, @Nullable Function oldFunction) {
        final Function function = add(builder);
        if (oldFunction == null) {
            bus.post(new AddedEvent(function));
        } else {
            bus.post(new ChangedEvent(oldFunction, function));
        }
    }

    @Override
    public void init() {
        Check.isNotMainThread();
        try {
            migrateOldFunctions();

            addSafely(new CustomFunction.Builder(true, "log", Arrays.asList("base", "x"), "ln(x)/ln(base)"));
            addSafely(new CustomFunction.Builder(true, "√3", Collections.singletonList("x"), "x^(1/3)"));
            addSafely(new CustomFunction.Builder(true, "√4", Collections.singletonList("x"), "x^(1/4)"));
            addSafely(new CustomFunction.Builder(true, "√n", Arrays.asList("x", "n"), "x^(1/n)"));
            addSafely(new CustomFunction.Builder(true, "re", Collections.singletonList("x"), "(x+conjugate(x))/2"));
            addSafely(new CustomFunction.Builder(true, "im", Collections.singletonList("x"), "(x-conjugate(x))/(2*i)"));

            for (CppFunction function : loadEntities(CppFunction.JSON_CREATOR)) {
                addSafely(function.toJsclBuilder());
            }
        } finally {
            setInitialized();
        }
    }

    @Override
    public void remove(@Nonnull Function function) {
        super.remove(function);
        bus.post(new RemovedEvent(function));
    }

    @Nullable
    @Override
    protected Jsonable toJsonable(@NonNull Function function) {
        if (function instanceof IFunction) {
            return CppFunction.builder((IFunction) function).build();
        }
        return null;
    }

    private void migrateOldFunctions() {
        final String xml = preferences.getString(OldFunctions.PREFS_KEY, null);
        if (isEmpty(xml)) {
            return;
        }
        try {
            final Serializer serializer = new Persister();
            final OldFunctions oldFunctions = serializer.read(OldFunctions.class, xml);
            if (oldFunctions != null) {
                List<CppFunction> functions = OldFunctions.toCppFunctions(oldFunctions);
                // todo serso: fix multiplication sign issue
                FileSaver.save(getEntitiesFile(), Json.toJson(functions).toString());
            }
            preferences.edit().remove(OldFunctions.PREFS_KEY).apply();
        } catch (Exception e) {
            errorReporter.onException(e);
        }
    }

    @Override
    @NonNull
    protected File getEntitiesFile() {
        return new File(application.getFilesDir(), "functions.json");
    }

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    @Override
    public Category getCategory(@Nonnull Function function) {
        return Entities.getCategory(function, FunctionCategory.values());
    }

    @Nullable
    @Override
    public String getDescription(@Nonnull String name) {
        final Function function = get(name);

        String description = null;
        if (function instanceof CustomFunction) {
            description = ((CustomFunction) function).getDescription();
        }

        if (!Strings.isEmpty(description)) {
            return description;
        }
        return super.getDescription(name);

    }

    public static final class RemovedEvent {
        @NonNull
        public final Function function;

        public RemovedEvent(@NonNull Function function) {
            this.function = function;
        }
    }

    public static final class AddedEvent {
        @NonNull
        public final Function function;

        public AddedEvent(@NonNull Function function) {
            this.function = function;
        }
    }

    public static final class ChangedEvent {
        @NonNull
        public final Function oldFunction;
        @NonNull
        public final Function newFunction;

        public ChangedEvent(@NonNull Function oldFunction, @NonNull Function newFunction) {
            this.oldFunction = oldFunction;
            this.newFunction = newFunction;
        }
    }
}
