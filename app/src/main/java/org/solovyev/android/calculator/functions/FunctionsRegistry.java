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

package org.solovyev.android.calculator.functions;

import android.support.annotation.NonNull;
import jscl.JsclMathEngine;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IFunction;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.entities.BaseEntitiesRegistry;
import org.solovyev.android.calculator.entities.Category;
import org.solovyev.android.calculator.entities.Entities;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.json.Jsonable;
import org.solovyev.android.io.FileSaver;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.*;

import static android.text.TextUtils.isEmpty;

@Singleton
public class FunctionsRegistry extends BaseEntitiesRegistry<Function> {

    {
        addDescription("sin", R.string.c_fun_description_sin);
        addDescription("cos", R.string.c_fun_description_cos);
        addDescription("tan", R.string.c_fun_description_tan);
        addDescription("cot", R.string.c_fun_description_cot);
        addDescription("asin", R.string.c_fun_description_asin);
        addDescription("acos", R.string.c_fun_description_acos);
        addDescription("atan", R.string.c_fun_description_atan);
        addDescription("acot", R.string.c_fun_description_acot);
        addDescription("ln", R.string.c_fun_description_ln);
        addDescription("lg", R.string.c_fun_description_lg);
        addDescription("log", R.string.c_fun_description_log);
        addDescription("exp", R.string.c_fun_description_exp);
        addDescription("√", R.string.c_fun_description_sqrt);
        addDescription("sqrt", R.string.c_fun_description_sqrt);
        addDescription("cubic", R.string.c_fun_description_cubic);
        addDescription("abs", R.string.c_fun_description_abs);
        addDescription("sgn", R.string.c_fun_description_sgn);
        addDescription("eq", R.string.c_fun_description_eq);
        addDescription("le", R.string.c_fun_description_le);
        addDescription("ge", R.string.c_fun_description_ge);
        addDescription("ne", R.string.c_fun_description_ne);
        addDescription("lt", R.string.c_fun_description_lt);
        addDescription("gt", R.string.c_fun_description_gt);
        addDescription("rad", R.string.c_fun_description_rad);
        addDescription("dms", R.string.c_fun_description_dms);
        addDescription("deg", R.string.c_fun_description_deg);
    }

    @Inject
    public FunctionsRegistry(@Nonnull JsclMathEngine mathEngine) {
        super(mathEngine.getFunctionsRegistry());
    }

    public void addOrUpdate(@Nonnull Function newFunction, @Nullable Function oldFunction) {
        final Function function = addOrUpdate(newFunction);
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

            final List<CustomFunction.Builder> functions = new ArrayList<>();
            functions.add(new CustomFunction.Builder(true, "log", Arrays.asList("base", "x"), "ln(x)/ln(base)"));
            functions.add(new CustomFunction.Builder(true, "√3", Collections.singletonList("x"), "x^(1/3)"));
            functions.add(new CustomFunction.Builder(true, "√4", Collections.singletonList("x"), "x^(1/4)"));
            functions.add(new CustomFunction.Builder(true, "√n", Arrays.asList("x", "n"), "x^(1/n)"));
            functions.add(new CustomFunction.Builder(true, "re", Collections.singletonList("x"), "(x+conjugate(x))/2"));
            functions.add(new CustomFunction.Builder(true, "im", Collections.singletonList("x"), "(x-conjugate(x))/(2*i)"));

            for (CppFunction function : loadEntities(CppFunction.JSON_CREATOR)) {
                functions.add(function.toJsclBuilder());
            }
            addSafely(functions);
        } finally {
            setInitialized();
        }
    }

    /**
     * As some functions might depend on not-yet-loaded functions we need to try to add all functions first and then
     * re-run again if there are functions left. This process should continue until we can't add more functions
     * @param functions functions to add
     */
    private void addSafely(@Nonnull List<CustomFunction.Builder> functions) {
        final List<Exception> exceptions = new ArrayList<>();
        while (functions.size() > 0) {
            final int sizeBefore = functions.size();
            // prepare exceptions list for new round
            exceptions.clear();
            addSafely(functions, exceptions);
            final int sizeAfter = functions.size();
            if (sizeBefore == sizeAfter) {
                break;
            }
        }

        if (functions.size() > 0) {
            // report exceptions
            for (Exception exception : exceptions) {
                errorReporter.onException(exception);
            }
        }
    }

    private void addSafely(@Nonnull List<CustomFunction.Builder> functions, @Nonnull List<Exception> exceptions) {
        for (Iterator<CustomFunction.Builder> iterator = functions.iterator(); iterator.hasNext(); ) {
            final CustomFunction.Builder function = iterator.next();
            try {
                addSafely(function.create());
                iterator.remove();
            } catch (Exception e) {
                exceptions.add(e);
            }
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
        return new File(filesDir, "functions.json");
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
