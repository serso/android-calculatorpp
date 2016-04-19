package org.solovyev.android.calculator.view;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import org.solovyev.android.Check;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public class EditTextCompat extends TextInputEditText {

    @Nullable
    private static Method setShowSoftInputOnFocusMethod;
    private static boolean setShowSoftInputOnFocusMethodChecked;

    public EditTextCompat(Context context) {
        super(context);
    }

    public EditTextCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static void insert(CharSequence text, EditText view) {
        final Editable e = view.getText();
        final int start = Math.max(0, view.getSelectionStart());
        final int end = Math.max(0, view.getSelectionEnd());
        e.replace(Math.min(start, end), Math.max(start, end), text);
    }

    public void dontShowSoftInputOnFocusCompat() {
        setShowSoftInputOnFocusCompat(false);
    }

    public void setShowSoftInputOnFocusCompat(boolean show) {
        Check.isMainThread();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(show);
        } else {
            dontShowSoftInputOnFocusPreLollipop(show);
        }
    }

    private void dontShowSoftInputOnFocusPreLollipop(boolean show) {
        final Method method = getSetShowSoftInputOnFocusMethod();
        if (method == null) {
            disableSoftInputFromAppearing();
            return;
        }
        try {
            method.invoke(this, show);
        } catch (Exception e) {
            Log.w("EditTextCompat", e.getMessage(), e);
        }
    }

    @Nullable
    private Method getSetShowSoftInputOnFocusMethod() {
        if (setShowSoftInputOnFocusMethodChecked) {
            return setShowSoftInputOnFocusMethod;
        }
        setShowSoftInputOnFocusMethodChecked = true;
        try {
            setShowSoftInputOnFocusMethod = EditText.class.getMethod("setShowSoftInputOnFocus", boolean.class);
            setShowSoftInputOnFocusMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            Log.d("EditTextCompat", "setShowSoftInputOnFocus was not found...");
        }
        return setShowSoftInputOnFocusMethod;
    }

    public void disableSoftInputFromAppearing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setRawInputType(InputType.TYPE_CLASS_TEXT);
            setTextIsSelectable(true);
        } else {
            setRawInputType(InputType.TYPE_NULL);
            setFocusable(true);
        }
    }
}
