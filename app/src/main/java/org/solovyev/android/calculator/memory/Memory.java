package org.solovyev.android.calculator.memory;

import android.os.Handler;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.otto.Bus;

import org.solovyev.android.Check;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.AppModule;
import org.solovyev.android.calculator.Notifier;
import org.solovyev.android.calculator.Runnables;
import org.solovyev.android.calculator.ToJsclTextProcessor;
import org.solovyev.android.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Lazy;
import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JsclInteger;
import jscl.text.ParseException;

@Singleton
public class Memory {

    @NonNull
    private static final Generic EMPTY = numeric(Expression.valueOf(JsclInteger.ZERO));
    @NonNull
    private final FileSystem fileSystem;
    @NonNull
    private final Lazy<File> filesDir;
    @NonNull
    private final WriteTask writeTask = new WriteTask();
    @NonNull
    private final Runnables whenLoadedRunnables = new Runnables();
    @NonNull
    private final Handler handler;
    @Inject
    Notifier notifier;
    @Inject
    ToJsclTextProcessor jsclProcessor;
    @Named(AppModule.THREAD_BACKGROUND)
    @Inject
    Executor backgroundThread;
    @Inject
    Bus bus;
    @NonNull
    private Generic value = EMPTY;
    private boolean loaded;

    @Inject
    public Memory(@NonNull @Named(AppModule.THREAD_INIT) Executor initThread, @NonNull FileSystem fileSystem, @NonNull @Named(AppModule.DIR_FILES)
            Lazy<File> filesDir, @NonNull Handler handler) {
        this.fileSystem = fileSystem;
        this.filesDir = filesDir;
        this.handler = handler;
        initThread.execute(new Runnable() {
            @Override
            public void run() {
                initAsync();
            }
        });
    }

    @NonNull
    private static Generic numeric(@NonNull Generic generic) {
        try {
            return generic.numeric();
        } catch (RuntimeException e) {
            return generic;
        }
    }

    private void initAsync() {
        Check.isNotMainThread();
        final Generic value = loadValue();
        handler.post(new Runnable() {
            @Override
            public void run() {
                onLoaded(value);
            }
        });
    }

    private void onLoaded(@NonNull Generic value) {
        this.value = value;
        this.loaded = true;
        this.whenLoadedRunnables.run();
    }

    @NonNull
    private Generic loadValue() {
        Check.isNotMainThread();
        try {
            final CharSequence value = fileSystem.read(getFile());
            return TextUtils.isEmpty(value) ? EMPTY : numeric(Expression.valueOf(value.toString()));
        } catch (IOException | ParseException e) {
            Log.e(App.TAG, e.getMessage(), e);
        }
        return EMPTY;
    }

    public void add(@NonNull final Generic that) {
        Check.isMainThread();
        if (!loaded) {
            postAdd(that);
            return;
        }
        try {
            setValue(value.add(that));
        } catch (RuntimeException e) {
            notifier.showMessage(e);
        }
    }

    private void postAdd(@NonNull final Generic that) {
        whenLoadedRunnables.add(new Runnable() {
            @Override
            public void run() {
                add(that);
            }
        });
    }

    public void subtract(@NonNull final Generic that) {
        Check.isMainThread();
        if (!loaded) {
            postSubtract(that);
            return;
        }
        try {
            setValue(value.subtract(that));
        } catch (RuntimeException e) {
            notifier.showMessage(e);
        }
    }

    private void postSubtract(@NonNull final Generic that) {
        whenLoadedRunnables.add(new Runnable() {
            @Override
            public void run() {
                subtract(that);
            }
        });
    }

    @NonNull
    private String getValue() {
        Check.isTrue(loaded);
        try {
            return value.toString();
        } catch (RuntimeException e) {
            Log.w(App.TAG, e.getMessage(), e);
        }
        return "";
    }

    private void setValue(@NonNull Generic newValue) {
        Check.isTrue(loaded);
        value = numeric(newValue);
        handler.removeCallbacks(writeTask);
        handler.postDelayed(writeTask, 3000L);
        show();
    }

    private void show() {
        notifier.showMessage(getValue());
    }

    public void clear() {
        Check.isMainThread();
        if (!loaded) {
            postClear();
            return;
        }
        setValue(EMPTY);
    }

    private void postClear() {
        whenLoadedRunnables.add(new Runnable() {
            @Override
            public void run() {
                clear();
            }
        });
    }

    @Nonnull
    private File getFile() {
        return new File(filesDir.get(), "memory.txt");
    }

    public void requestValue() {
        if (!loaded) {
            postValue();
            return;
        }
        bus.post(new ValueReadyEvent(getValue()));
    }

    private void postValue() {
        whenLoadedRunnables.add(new Runnable() {
            @Override
            public void run() {
                requestValue();
            }
        });
    }

    public void requestShow() {
        if (!loaded) {
            postShow();
            return;
        }
        show();
    }

    private void postShow() {
        whenLoadedRunnables.add(new Runnable() {
            @Override
            public void run() {
                requestShow();
            }
        });
    }

    public static final class ValueReadyEvent {
        @NonNull
        public final String value;

        public ValueReadyEvent(@NonNull String value) {
            this.value = value;
        }
    }

    private class WriteTask implements Runnable {
        @Override
        public void run() {
            Check.isMainThread();
            if (!loaded) {
                return;
            }
            final String value = getValue();
            backgroundThread.execute(new Runnable() {
                @Override
                public void run() {
                    fileSystem.writeSilently(getFile(), prepareExpression(value));
                }
            });
        }

        @NonNull
        private String prepareExpression(@NonNull String value) {
            try {
                return jsclProcessor.process(value).getValue();
            } catch (org.solovyev.android.calculator.ParseException ignored) {
                return value;
            }
        }
    }
}
