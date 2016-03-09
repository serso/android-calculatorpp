package org.solovyev.android.calculator;

import android.support.annotation.NonNull;
import org.solovyev.android.Check;

import java.util.ArrayList;
import java.util.List;

public class Runnables implements Runnable {
    @NonNull
    private final List<Runnable> list = new ArrayList<>();
    @Override
    public void run() {
        Check.isMainThread();
        for (Runnable runnable : list) {
            runnable.run();
        }
        list.clear();
    }

    public void add(@NonNull Runnable runnable) {
        Check.isMainThread();
        list.add(runnable);
    }
}
