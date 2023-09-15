package org.solovyev.android.io;

import androidx.annotation.NonNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class BaseIoSaver {

    @NonNull
    private final CharSequence data;

    protected BaseIoSaver(@NonNull CharSequence data) {
        this.data = data;
    }

    public void save() throws IOException {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(getOutputStream());
            out.write(data.toString());
        } finally {
            Io.close(out);
        }
    }

    @NonNull
    protected abstract FileOutputStream getOutputStream() throws FileNotFoundException;
}
