package org.solovyev.android.calculator;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import android.util.Log;

import org.solovyev.android.Check;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

class TaskExecutor {

    private class Task implements Runnable {

        @NonNull
        private final Runnable runnable;
        private final boolean cancellable;
        @NonNull
        private final Future<?> future;

        private Task(@NonNull Runnable runnable, boolean cancellable) {
            this.runnable = runnable;
            this.cancellable = cancellable;
            this.future = executor.submit(this);
        }

        @Override
        public void run() {
            Log.d(TAG, "Running task: " + System.identityHashCode(this) + " on "
                    + Thread.currentThread().getName());
            try {
                runnable.run();
            } finally {
                onTaskFinished(this);
            }
        }

        boolean isFinished() {
            return future.isDone() || future.isCancelled();
        }

        void cancel() {
            Log.d(TAG, "Task cancelled: " + System.identityHashCode(this));
            Check.isTrue(cancellable);
            future.cancel(true);
        }
    }

    private static final int MAX_TASKS = 5;
    @NonNull
    private static final String TAG = "TaskExecutor";
    @NonNull
    private final List<Task> tasks = new ArrayList<>();
    @NonNull
    private final ExecutorService executor = makeExecutor();
    private boolean synchronous = false;

    @NonNull
    private static ExecutorService makeExecutor() {
        return Executors.newCachedThreadPool(
                new ThreadFactory() {
                    @NonNull
                    private final AtomicInteger counter = new AtomicInteger();

                    @Override
                    public Thread newThread(@Nonnull Runnable r) {
                        return new Thread(r, "Task #" + counter.getAndIncrement());
                    }
                });
    }

    void execute(@NonNull Runnable runnable, boolean cancellable) {
        Check.isMainThread();
        if (synchronous) {
            runnable.run();
            return;
        }
        synchronized (tasks) {
            if (tasks.size() >= MAX_TASKS) {
                for (int i = 0; i < tasks.size(); i++) {
                    final Task task = tasks.get(i);
                    if (task.cancellable) {
                        tasks.remove(i);
                        task.cancel();
                        break;
                    }
                }
            }
        }
        onTaskStarted(new Task(runnable, cancellable));
    }

    private void onTaskStarted(@NonNull Task task) {
        synchronized (tasks) {
            if (!task.isFinished()) {
                Log.d(TAG, "Task added: " + System.identityHashCode(task));
                tasks.add(task);
            }
        }
    }

    private void onTaskFinished(@NonNull Task task) {
        synchronized (tasks) {
            Log.d(TAG, "Task removed: " + System.identityHashCode(task));
            tasks.remove(task);
        }
    }

    @VisibleForTesting
    void setSynchronous() {
        synchronous = true;
    }
}
