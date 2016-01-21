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

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.Check;
import org.solovyev.android.calculator.function.CppFunction;
import org.solovyev.android.calculator.json.Json;
import org.solovyev.android.calculator.model.OldFunction;
import org.solovyev.android.calculator.model.OldFunctions;
import org.solovyev.android.io.FileSaver;
import org.solovyev.common.JBuilder;
import org.solovyev.common.text.Strings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import jscl.JsclMathEngine;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;

import static android.text.TextUtils.isEmpty;

@Singleton
public class FunctionsRegistry extends BaseEntitiesRegistry<Function, OldFunction> {

    @Nonnull
    private static final Map<String, String> substitutes = new HashMap<>();
    @Nonnull
    private static final String FUNCTION_DESCRIPTION_PREFIX = "c_fun_description_";

    static {
        substitutes.put("√", "sqrt");
    }

    @NonNull
    private final WriteTask writeTask = new WriteTask();
    @Inject
    @Named(AppModule.THREAD_BACKGROUND)
    Executor backgroundThread;
    @Inject
    Handler handler;
    @Inject
    ErrorReporter errorReporter;
    @Inject
    Calculator calculator;
    @Inject
    SharedPreferences preferences;
    @Inject
    Application application;
    @Inject
    Bus bus;

    @Inject
    public FunctionsRegistry(@Nonnull JsclMathEngine mathEngine) {
        super(mathEngine.getFunctionsRegistry(), FUNCTION_DESCRIPTION_PREFIX, null);
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
        migrateOldFunctions();

        add(new CustomFunction.Builder(true, "log", Arrays.asList("base", "x"), "ln(x)/ln(base)"));
        add(new CustomFunction.Builder(true, "√3", Collections.singletonList("x"), "x^(1/3)"));
        add(new CustomFunction.Builder(true, "√4", Collections.singletonList("x"), "x^(1/4)"));
        add(new CustomFunction.Builder(true, "√n", Arrays.asList("x", "n"), "x^(1/n)"));
        add(new CustomFunction.Builder(true, "re", Collections.singletonList("x"), "(x+conjugate(x))/2"));
        add(new CustomFunction.Builder(true, "im", Collections.singletonList("x"), "(x-conjugate(x))/(2*i)"));

        for (CppFunction function : loadFunctions()) {
            add(function.toCustomFunctionBuilder());
        }
    }

    @Override
    public void remove(@Nonnull Function function) {
        super.remove(function);
        bus.post(new RemovedEvent(function));
        save();
    }

    @Override
    public Function add(@Nonnull JBuilder<? extends Function> result) {
        final Function function = super.add(result);
        // todo serso: don't save while we're initializing
        save();
        return function;
    }

    @NonNull
    private List<CppFunction> loadFunctions() {
        try {
            return Json.load(getFunctionsFile(), CppFunction.JSON_CREATOR);
        } catch (IOException | JSONException e) {
            errorReporter.onException(e);
        }
        return Collections.emptyList();
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
                FileSaver.save(getFunctionsFile(), Json.toJson(functions).toString());
            }
            preferences.edit().remove(OldFunctions.PREFS_KEY).apply();
        } catch (Exception e) {
            errorReporter.onException(e);
        }
    }

    @Override
    public synchronized void save() {
        handler.removeCallbacks(writeTask);
        handler.postDelayed(writeTask, 500);
    }

    @NonNull
    private File getFunctionsFile() {
        return new File(application.getFilesDir(), "functions.json");
    }

    @Nonnull
    @Override
    protected Map<String, String> getSubstitutes() {
        return substitutes;
    }

    @Override
    public String getCategory(@Nonnull Function function) {
        for (FunctionCategory category : FunctionCategory.values()) {
            if (category.isInCategory(function)) {
                return category.name();
            }
        }

        return null;
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

    @Nonnull
    @Override
    protected JBuilder<? extends Function> createBuilder(@Nonnull OldFunction function) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected OldFunction transform(@Nonnull Function function) {
        if (function instanceof CustomFunction) {
            return OldFunction.fromIFunction((CustomFunction) function);
        } else {
            return null;
        }
    }

    @Nonnull
    @Override
    protected PersistedEntitiesContainer<OldFunction> createPersistenceContainer() {
        return new OldFunctions();
    }

    private class WriteTask implements Runnable {

        @Override
        public void run() {
            Check.isMainThread();
            final List<CppFunction> functions = new ArrayList<>();
            for (Function function : getEntities()) {
                if (function.isSystem()) {
                    continue;
                }
                if (function instanceof CustomFunction) {
                    functions.add(CppFunction.builder((CustomFunction) function).build());
                }
            }
            backgroundThread.execute(new Runnable() {
                @Override
                public void run() {
                    final JSONArray array = Json.toJson(functions);
                    try {
                        FileSaver.save(getFunctionsFile(), array.toString());
                    } catch (IOException e) {
                        errorReporter.onException(e);
                    }
                }
            });
        }
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
