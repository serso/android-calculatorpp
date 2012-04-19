package org.solovyev.android.calculator;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 3/5/12
 * Time: 10:23 PM
 */
public class CalculationServiceImpl extends Service implements ICalculationService {

    @NotNull
    private ServiceHandler handler;

    public CalculationServiceImpl() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, ".CalculationService.onStartCommand", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        final Message msg = handler.obtainMessage();
        msg.arg1 = startId;
        handler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onCreate() {
        Toast.makeText(this, ".CalculationService.onCreate", Toast.LENGTH_SHORT).show();
        // first time initialization

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        final HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        handler = new ServiceHandler(thread.getLooper());

    }

    @Override
    public void onDestroy() {
        // last time call
        Toast.makeText(this, ".CalculationService.onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder<ICalculationService>(this);
    }

    private final class ServiceHandler extends Handler {

        private ServiceHandler(@NotNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NotNull Message msg) {
            Toast.makeText(CalculationServiceImpl.this, "Doing job!", Toast.LENGTH_SHORT).show();
            stopSelf(msg.arg1);
        }
    }
}
