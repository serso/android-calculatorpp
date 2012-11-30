package org.solovyev.android.calculator;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.view.VibratorContainer;

/**
 * User: serso
 * Date: 11/18/12
 * Time: 6:05 PM
 */
public class AndroidCalculatorKeyboard implements CalculatorKeyboard {

    @NotNull
    private final CalculatorKeyboard calculatorKeyboard;

    @NotNull
    private final Context context;

    private VibratorContainer vibrator;

    public AndroidCalculatorKeyboard(@NotNull Application application,
                                     @NotNull CalculatorKeyboard calculatorKeyboard) {
        this.context = application;
        this.calculatorKeyboard = calculatorKeyboard;
    }

    @Override
    public void buttonPressed(@Nullable String text) {
        vibrate();
        calculatorKeyboard.buttonPressed(text);
    }

    private void vibrate() {
        if (this.vibrator == null) {
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            final Vibrator vibrator = (Vibrator) context.getSystemService(Activity.VIBRATOR_SERVICE);

            this.vibrator = new VibratorContainer(vibrator, preferences, 0.5f);
        }

        this.vibrator.vibrate();
    }

    @Override
    public void roundBracketsButtonPressed() {
        vibrate();
        calculatorKeyboard.roundBracketsButtonPressed();
    }

    @Override
    public void pasteButtonPressed() {
        vibrate();
        calculatorKeyboard.pasteButtonPressed();
    }

    @Override
    public void clearButtonPressed() {
        vibrate();
        calculatorKeyboard.clearButtonPressed();
    }

    @Override
    public void copyButtonPressed() {
        vibrate();
        calculatorKeyboard.copyButtonPressed();
    }

    @Override
    public void moveCursorLeft() {
        vibrate();
        calculatorKeyboard.moveCursorLeft();
    }

    @Override
    public void moveCursorRight() {
        vibrate();
        calculatorKeyboard.moveCursorRight();
    }
}
