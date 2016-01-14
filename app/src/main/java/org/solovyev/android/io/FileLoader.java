package org.solovyev.android.io;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader extends BaseIoLoader {

    @NonNull
    private final File file;

    public FileLoader(@NonNull File file) {
        this.file = file;
    }

    @Nullable
    public static CharSequence load(@NonNull File file) throws IOException {
        final FileLoader loader = new FileLoader(file);
        return loader.load();
    }

    @Nullable
    @Override
    protected InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }
}
