package org.solovyev.android.calculator.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import org.solovyev.android.Check;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

public class EditTextCompat extends EditText {

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditTextCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void dontShowSoftInputOnFocusCompat() {
        Check.isMainThread();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(false);
        } else {
            dontShowSoftInputOnFocusPreLollipop();
        }
    }

    private void dontShowSoftInputOnFocusPreLollipop() {
        final Method method = getSetShowSoftInputOnFocusMethod();
        if (method == null) {
            disableSoftInputFromAppearing();
            return;
        }
        try {
            method.invoke(this, false);
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
            Log.w("EditTextCompat", e.getMessage(), e);
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
