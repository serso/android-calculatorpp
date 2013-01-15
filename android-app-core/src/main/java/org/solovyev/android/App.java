package org.solovyev.android;

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

    @NotNull
    public static App getInstance() {
        return instance;
    }


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


    public void init(@NotNull Application application) {
        if (!initialized) {
            this.application = application;
            this.uiThreadExecutor = new UiThreadExecutor();

            this.initialized = true;
        }
    }

    @NotNull
    public <A extends Application> A getApplication() {
        return (A) application;
    }

    @NotNull
    public DelayedExecutor getUiThreadExecutor() {
        return uiThreadExecutor;
    }
}
