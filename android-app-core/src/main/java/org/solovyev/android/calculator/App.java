package org.solovyev.android.calculator;

import android.app.Application;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.DelayedExecutor;

/**
 * User: serso
 * Date: 12/1/12
 * Time: 3:58 PM
 */
public final class App {

    /*
    **********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */
    @NotNull
    public static final App instance = new App();

    /*
    **********************************************************************
    *
    *                           FIELDS
    *
    **********************************************************************
    */

    @NotNull
    private volatile Application application;

    @NotNull
    private volatile DelayedExecutor uiThreadExecutor;

    private volatile boolean initialized;

    private App() {
    }

    @NotNull
    public static App getInstance() {
        return instance;
    }

    @NotNull
    public Application getApplication() {
        return application;
    }

    @NotNull
    public DelayedExecutor getUiThreadExecutor() {
        return uiThreadExecutor;
    }

    void init(@NotNull Application application) {
        if (!initialized) {
            this.application = application;
            this.uiThreadExecutor = new UiThreadExecutor();

            this.initialized = true;
        }
    }
}
