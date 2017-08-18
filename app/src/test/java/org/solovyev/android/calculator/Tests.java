package org.solovyev.android.calculator;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.solovyev.android.calculator.entities.BaseEntitiesRegistry;
import org.solovyev.android.calculator.functions.FunctionsRegistry;
import org.solovyev.android.calculator.operators.OperatorsRegistry;
import org.solovyev.android.calculator.operators.PostfixFunctionsRegistry;
import org.solovyev.android.io.FileSystem;

import java.io.File;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import dagger.Lazy;
import jscl.JsclMathEngine;

public class Tests {

    @NonNull
    public static Executor sameThreadExecutor() {
        return new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                command.run();
            }
        };
    }

    @NonNull
    public static Engine makeEngine() {
        final JsclMathEngine mathEngine = JsclMathEngine.getInstance();
        mathEngine.setGroupingSeparator(' ');
        final Engine engine = new Engine(mathEngine);
        engine.postfixFunctionsRegistry = init(new PostfixFunctionsRegistry(mathEngine));
        engine.functionsRegistry = init(new FunctionsRegistry(mathEngine));
        engine.variablesRegistry = init(new VariablesRegistry(mathEngine));
        engine.operatorsRegistry = init(new OperatorsRegistry(mathEngine));
        engine.errorReporter = new ErrorReporter() {
            @Override
            public void onException(@Nonnull Throwable e) {
                throw new AssertionError(e);
            }

            @Override
            public void onError(@Nonnull String message) {
                throw new AssertionError(message);
            }
        };
        engine.initAsync();
        return engine;
    }

    @NonNull
    private static <T extends BaseEntitiesRegistry<?>> T init(@NonNull T registry) {
        registry.preferences = Mockito.mock(SharedPreferences.class);
        registry.filesDir = new Lazy<File>() {
            @Override
            public File get() {
                return RuntimeEnvironment.application.getFilesDir();
            }
        };
        registry.fileSystem = Mockito.mock(FileSystem.class);
        registry.handler = new Handler();
        return registry;
    }
}
