package org.solovyev.android.calculator.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import org.solovyev.android.Check;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public class EditTextCompat extends EditText {

    @Nullable
    private Method setShowSoftInputOnFocusMethod;

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

    public void setShowSoftInputOnFocusCompat(boolean show) {
        Check.isMainThread();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setShowSoftInputOnFocus(show);
        } else {
            setShowSoftInputOnFocusPreLollipop(show);
        }
    }

    private void setShowSoftInputOnFocusPreLollipop(boolean show) {
        try {
            if (setShowSoftInputOnFocusMethod == null) {
                setShowSoftInputOnFocusMethod = EditText.class.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocusMethod.setAccessible(true);
            }
            setShowSoftInputOnFocusMethod.invoke(this, show);
        } catch (Exception e) {
            Log.w("EditTextCompat", e.getMessage(), e);
        }
    }
}
