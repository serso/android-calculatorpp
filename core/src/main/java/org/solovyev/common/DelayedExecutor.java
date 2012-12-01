package org.solovyev.common;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * User: serso
 * Date: 12/1/12
 * Time: 4:10 PM
 */
public interface DelayedExecutor extends Executor {

    void execute(@NotNull Runnable command, long delay, @NotNull TimeUnit delayUnit);
}
