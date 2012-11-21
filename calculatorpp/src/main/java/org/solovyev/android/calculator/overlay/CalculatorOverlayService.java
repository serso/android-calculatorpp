package org.solovyev.android.calculator.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorViewState;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.external.DefaultExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorIntentHandler;
import org.solovyev.android.calculator.external.ExternalCalculatorStateUpdater;
import org.solovyev.android.calculator.widget.CalculatorWidgetHelper;
import org.solovyev.android.calculator.widget.WidgetButton;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 9:42 PM
 */
public class CalculatorOverlayService extends Service implements ExternalCalculatorStateUpdater {

    @Nullable
    private View view;

    @NotNull
    private final ExternalCalculatorIntentHandler intentHandler = new DefaultExternalCalculatorIntentHandler(this);

    @Nullable
    private static String cursorColor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.overlay_layout, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                300,
                450,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        for (final WidgetButton widgetButton : WidgetButton.values()) {
            final View button = view.findViewById(widgetButton.getButtonId());
            if (button != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        widgetButton.onClick(CalculatorOverlayService.this);
                    }
                });
            }
        }

        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(view, params);

        startCalculatorListening();
    }

    private void startCalculatorListening() {
        CalculatorWidgetHelper.addExternalListener(getIntentListenerClass());
    }

    @NotNull
    private Class<?> getIntentListenerClass() {
        return CalculatorOverlayBroadcastReceiver.class;
    }

    private void stopCalculatorListening() {
        CalculatorWidgetHelper.removeExternalListener(getIntentListenerClass());
    }

    @Override
    public void onDestroy() {
        stopCalculatorListening();

        if (view != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(view);
            view = null;
        }

        super.onDestroy();
    }

    @Override
    public void updateState(@NotNull Context context, @NotNull CalculatorEditorViewState editorState, @NotNull CalculatorDisplayViewState displayState) {
        final View root = this.view;
        if (root != null) {
            updateDisplayState(context, root, displayState);
            updateEditorState(context, root, editorState);
        }
    }

    private static void updateDisplayState(@NotNull Context context, @NotNull View root, @NotNull CalculatorDisplayViewState displayState) {
        final TextView calculatorDisplayView = (TextView) root.findViewById(R.id.calculator_display);
        if (calculatorDisplayView != null) {
            if (displayState.isValid()) {
                calculatorDisplayView.setText(displayState.getText());
                calculatorDisplayView.setTextColor(context.getResources().getColor(R.color.cpp_default_text_color));
            } else {
                calculatorDisplayView.setTextColor(context.getResources().getColor(R.color.cpp_display_error_text_color));
            }
        }
    }

    private static void updateEditorState(@NotNull Context context, @NotNull View root, @NotNull CalculatorEditorViewState editorState) {
        final TextView calculatorEditorView = (TextView) root.findViewById(R.id.calculator_editor);

        if (calculatorEditorView != null) {
            String text = editorState.getText();

            CharSequence newText = text;
            int selection = editorState.getSelection();
            if (selection >= 0 && selection <= text.length()) {
                // inject cursor
                newText = Html.fromHtml(text.substring(0, selection) + "<font color=\"#" + getCursorColor(context) + "\">|</font>" + text.substring(selection));
            }
            calculatorEditorView.setText(newText);
        }
    }

    @NotNull
    private static String getCursorColor(@NotNull Context context) {
        if (cursorColor == null) {
            cursorColor = Integer.toHexString(context.getResources().getColor(R.color.cpp_widget_cursor_color)).substring(2);
        }
        return cursorColor;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        if ( intent != null ) {
            intentHandler.onIntent(this, intent);
        }

        return result;
    }
}

