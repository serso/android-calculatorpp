package org.solovyev.android.calculator;

import android.app.Dialog;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.List;

public class KeyboardWindow {

    @Nullable
    private PopupWindow window;
    @Nullable
    private Dialog dialog;

    private static void hideIme(@NonNull View view) {
        final IBinder token = view.getWindowToken();
        if (token != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(token, 0);
        }
    }

    public void hide() {
        if (!isShown()) {
            return;
        }
        moveDialog(Gravity.CENTER);
        window.dismiss();
        window = null;
        dialog = null;
    }

    public void show(@NonNull KeyboardUi.User user, @Nullable Dialog dialog, @NonNull List<String> parameterNames) {
        if (isShown()) {
            return;
        }
        this.dialog = dialog;
        moveDialog(Gravity.TOP);
        final EditText editor = user.getEditor();
        hideIme(editor);
        final Context context = editor.getContext();
        final LinearLayout view = new LinearLayout(context);
        view.setOrientation(LinearLayout.VERTICAL);
        final int buttonSize = context.getResources().getDimensionPixelSize(R.dimen.cpp_clickable_area_size);
        final int keyboardSize = 5 * buttonSize;
        window = new PopupWindow(view, keyboardSize, keyboardSize);
        window.setClippingEnabled(false);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                window = null;
            }
        });
        // see http://stackoverflow.com/a/4713487/720489
        editor.post(new Runnable() {
            @Override
            public void run() {
                if (window == null) {
                    return;
                }
                if (editor.getWindowToken() != null) {
                    hideIme(editor);
                    final int inputWidth = editor.getWidth();
                    final int xOff = (inputWidth - keyboardSize) / 2;
                    window.setWidth(keyboardSize);
                    window.showAsDropDown(editor, xOff, 0);
                } else {
                    editor.postDelayed(this, 50);
                }
            }
        });
        new KeyboardUi(user, parameterNames).makeView();
    }

    public boolean isShown() {
        return window != null;
    }

    @SuppressWarnings("unchecked")
    public <V extends View> V getContentView() {
        return (V) window.getContentView();
    }

    public void moveDialog(int gravity) {
        if (dialog == null) {
            return;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = gravity;
        window.setAttributes(lp);
    }
}
