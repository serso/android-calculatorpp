package org.solovyev.android.calculator;

import android.support.annotation.NonNull;

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
}
