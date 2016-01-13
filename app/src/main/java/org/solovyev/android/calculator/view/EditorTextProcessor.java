package org.solovyev.android.calculator.view;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.solovyev.android.calculator.Preferences.Gui.colorDisplay;
import static org.solovyev.android.calculator.Preferences.Gui.theme;

public final class EditorTextProcessor implements TextProcessor<TextProcessorEditorResult, String>, SharedPreferences.OnSharedPreferenceChangeListener {

    private boolean highlightText = true;

    @Nullable
    private TextProcessor<TextProcessorEditorResult, String> textHighlighter;

    public EditorTextProcessor(@Nonnull SharedPreferences preferences) {
        preferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(preferences, colorDisplay.getKey());
    }

    @Nonnull
    @Override
    public TextProcessorEditorResult process(@Nonnull String text) throws CalculatorParseException {
        TextProcessorEditorResult result;

        if (highlightText) {

            try {
                final TextProcessorEditorResult processesText = getTextHighlighter().process(text);

                result = new TextProcessorEditorResult(processesText.getCharSequence(), processesText.getOffset());
            } catch (CalculatorParseException e) {
                // set raw text
                result = new TextProcessorEditorResult(text, 0);

                Log.e(this.getClass().getName(), e.getMessage(), e);
            }
        } else {
            result = new TextProcessorEditorResult(text, 0);
        }

        return result;
    }

    @Nonnull
    private TextProcessor<TextProcessorEditorResult, String> getTextHighlighter() {
        if (textHighlighter == null) {
            onSharedPreferenceChanged(App.getPreferences(), theme.getKey());
        }
        return textHighlighter;
    }

    public boolean isHighlightText() {
        return highlightText;
    }

    public void setHighlightText(boolean highlightText) {
        this.highlightText = highlightText;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (colorDisplay.isSameKey(key)) {
            setHighlightText(colorDisplay.getPreference(preferences));
        } else if (theme.isSameKey(key)) {
            final int color = getTextColor(preferences);
            textHighlighter = new TextHighlighter(color, true);
        }
    }

    private int getTextColor(@Nonnull SharedPreferences preferences) {
        final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(preferences);
        final Application application = App.getApplication();
        return theme.getTextColorFor(application).normal;
    }
}
