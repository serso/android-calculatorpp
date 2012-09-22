package org.solovyev.android.calculator;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.msg.Message;

/**
 * User: serso
 * Date: 9/22/12
 * Time: 2:00 PM
 */
public class AndroidCalculatorNotifier implements CalculatorNotifier {

    @NotNull
    private final Context context;

    public AndroidCalculatorNotifier(@NotNull Application application) {
        this.context = application;
    }

    @Override
    public void showMessage(@NotNull Message message) {
        Toast.makeText(context, message.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
