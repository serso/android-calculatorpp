package org.solovyev.android.calculator;

import android.support.annotation.NonNull;
import jscl.JsclMathEngine;
import org.solovyev.android.calculator.functions.FunctionsRegistry;
import org.solovyev.android.calculator.operators.OperatorsRegistry;
import org.solovyev.android.calculator.operators.PostfixFunctionsRegistry;

import java.util.concurrent.Executor;

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
        engine.postfixFunctionsRegistry = new PostfixFunctionsRegistry(mathEngine);
        engine.functionsRegistry = new FunctionsRegistry(mathEngine);
        engine.variablesRegistry = new VariablesRegistry(mathEngine);
        engine.operatorsRegistry = new OperatorsRegistry(mathEngine);
        return engine;
    }
}
