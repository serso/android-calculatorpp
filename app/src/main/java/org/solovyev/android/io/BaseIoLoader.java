package org.solovyev.android.io;

import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class BaseIoLoader {

    @Nullable
    public CharSequence load() throws IOException {
        BufferedReader reader = null;
        try {
            final InputStream is = getInputStream();
            if (is == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(is));
            final StringBuilder result = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                result.append(line).append("\n");
                line = reader.readLine();
            }
            return result;
        } finally {
            Io.close(reader);
        }
    }

    @Nullable
    protected abstract InputStream getInputStream() throws IOException;
}
