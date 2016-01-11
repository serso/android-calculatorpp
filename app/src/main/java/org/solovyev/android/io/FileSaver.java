package org.solovyev.android.io;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileSaver extends BaseFileSaver {

    @NonNull
    private final File file;

    private FileSaver(@NonNull File file, @NonNull CharSequence data) {
        super(data);
        this.file = file;
    }

    public static void save(@NonNull File file, @NonNull CharSequence data) {
        final FileSaver fileSaver = new FileSaver(file, data);
        fileSaver.save();
    }

    @NonNull
    protected FileOutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(file);
    }
}
