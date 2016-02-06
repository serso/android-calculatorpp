package org.solovyev.android.calculator;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import com.squareup.otto.Bus;
import com.squareup.otto.GeneratedHandlerFinder;
import dagger.Module;
import dagger.Provides;
import jscl.JsclMathEngine;
import org.solovyev.android.UiThreadExecutor;
import org.solovyev.android.checkout.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Provides
    @Singleton
    JsclMathEngine provideJsclMathEngine() {
        return JsclMathEngine.getInstance();
    }

    @Provides
    @Singleton
    Billing provideBilling() {
        return new Billing(application, new Billing.DefaultConfiguration() {
            @Nonnull
            @Override
            public String getPublicKey() {
                return CalculatorSecurity.getPK();
            }

            @Nullable
            @Override
            public Inventory getFallbackInventory(@Nonnull Checkout checkout, @Nonnull Executor onLoadExecutor) {
                if (RobotmediaDatabase.exists(application)) {
                    return new RobotmediaInventory(checkout, onLoadExecutor);
                } else {
                    return null;
                }
            }
        });
    }

    @Singleton
    @Provides
    Products provideProducts() {
        return Products.create().add(ProductTypes.IN_APP, Collections.singletonList("ad_free"));
    }

    @Singleton
    @Provides
    Typeface provideTypeface() {
        return Typeface.createFromAsset(application.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    private static class AppBus extends Bus {

        @NonNull
        private final Handler handler;

        public AppBus(@Nonnull Handler handler) {
            super(new GeneratedHandlerFinder());
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
