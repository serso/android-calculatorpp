package org.solovyev.android.io;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class BaseFileSaver implements Runnable {

    @NonNull
    private final CharSequence data;

    protected BaseFileSaver(@NonNull CharSequence data) {
        this.data = data;
    }

    public void save() {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(getOutputStream());
            out.write(data.toString());
        } catch (IOException e) {
            Log.e("FileSaver", e.getMessage(), e);
        } finally {
            Io.close(out);
        }
    }

    @NonNull
    protected abstract FileOutputStream getOutputStream() throws FileNotFoundException;

    @Override
    public void run() {
        save();
    }
}
