package org.solovyev.android.io;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.solovyev.android.calculator.ErrorReporter;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FileSystem {

    @Inject
    ErrorReporter errorReporter;

    @Inject
    public FileSystem() {
    }

    public boolean writeSilently(@NonNull File file, @NonNull String data) {
        try {
            write(file, data);
            return true;
        } catch (IOException e) {
            errorReporter.onException(e);
        }
        return false;
    }

    public void write(@NonNull File file, @NonNull String data) throws IOException {
        FileSaver.save(file, data);
    }

    @Nullable
    public CharSequence read(File file) throws IOException {
        return FileLoader.load(file);
    }
}
