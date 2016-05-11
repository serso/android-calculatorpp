package org.solovyev.android.calculator.feedback;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import org.solovyev.android.calculator.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FeedbackReporter {

    private final Application context;

    @Inject
    public FeedbackReporter(Application context) {
        this.context = context;
    }

    public void report() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"se.solovyev@gmail.com"});
        final String version = getVersion();
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.cpp_app_name) + " " + version + " // " + Build.MANUFACTURER + " " + Build.MODEL + " (" + Build.DEVICE + ") " + Build.VERSION.SDK_INT);
        intent.setType("plain/html");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("FeedbackReporter", e.getMessage(), e);
        }
    }

    private String getVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "x.x.x";
        }
    }
}
