package org.solovyev.android.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.squareup.otto.Bus;

import org.solovyev.android.UiThreadExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    // single thread, should be used during the startup
    public static final String THREAD_INIT = "thread-init";
    // UI application thread
    public static final String THREAD_UI = "thread-ui";
    // multiple threads
    public static final String THREAD_BACKGROUND = "thread-background";

    @NonNull
    private final Application application;

    public AppModule(@NonNull Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Handler provideHandler() {
        return new Handler(Looper.getMainLooper());
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    Bus provideBus(Handler handler) {
        return new AppBus(handler);
    }

    @Provides
    @Singleton
    SharedPreferences providePreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Calculator provideCalculator(Bus bus, @Named(THREAD_UI) Executor executor) {
        return new AndroidCalculator(application, bus, executor);
    }

    @Provides
    @Singleton
    @Named(THREAD_INIT)
    Executor provideInitThread() {
        return Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@Nonnull Runnable r) {
                return new Thread(r, "Init");
            }
        });
    }

    @Provides
    @Singleton
    @Named(THREAD_BACKGROUND)
    Executor provideBackgroundThread() {
        return Executors.newFixedThreadPool(5, new ThreadFactory() {
            @NonNull
            private final AtomicInteger counter = new AtomicInteger();
            @Override
            public Thread newThread(@Nonnull Runnable r) {
                return new Thread(r, "Background #" + counter.getAndIncrement());
            }
        });
    }

    @Provides
    @Singleton
    ErrorReporter provideErrorReporter() {
        return new AcraErrorReporter();
    }

    @Provides
    @Singleton
    @Named(THREAD_UI)
    Executor provideUiThread() {
        return new UiThreadExecutor();
    }

    private static class AppBus extends Bus {

        @NonNull
        private final Handler handler;

        public AppBus(@Nonnull Handler handler) {
            this.handler = handler;
        }

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AppBus.super.post(event);
                }
            });
        }
    }
}
