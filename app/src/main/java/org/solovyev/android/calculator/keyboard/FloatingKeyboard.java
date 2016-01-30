package org.solovyev.android.calculator.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.EditText;

public interface FloatingKeyboard {
    int getRowsCount(boolean landscape);

    int getColumnsCount(boolean landscape);

    void makeView(boolean landscape);

    @NonNull
    User getUser();

    interface User {
        @NonNull
        Context getContext();

        @NonNull
        Resources getResources();

        @NonNull
        EditText getEditor();

        @NonNull
        ViewGroup getKeyboard();

        void done();

        void showIme();
    }
}
