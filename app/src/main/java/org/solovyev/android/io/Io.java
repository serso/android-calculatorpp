package org.solovyev.android.io;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

public final class Io {

    private Io() {
    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            Log.e("Io", e.getMessage(), e);
        }
    }
}
