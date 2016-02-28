package org.solovyev.android.calculator.view;

import android.app.Application;
import android.content.SharedPreferences;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.Engine;
import org.solovyev.android.calculator.Preferences;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.Preferences.Gui.colorDisplay;
import static org.solovyev.android.calculator.Preferences.Gui.theme;

public final class EditorTextProcessor implements TextProcessor<TextProcessorEditorResult, String>, SharedPreferences.OnSharedPreferenceChangeListener {

    private boolean highlightText = true;

    @Nullable
    private TextHighlighter textHighlighter;
    @Nonnull
    private final SharedPreferences preferences;
    @Nonnull
    private final Engine engine;

    public EditorTextProcessor(@Nonnull SharedPreferences preferences, @Nonnull Engine engine) {
        this.preferences = preferences;
        this.engine = engine;
        preferences.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(preferences, colorDisplay.getKey());
    }

    @Nonnull
    @Override
    public TextProcessorEditorResult process(@Nonnull String text) {
        if (!highlightText) {
            return new TextProcessorEditorResult(text, 0);
        }
        final TextProcessorEditorResult processesText = getTextHighlighter().process(text);
        return new TextProcessorEditorResult(processesText.getCharSequence(), processesText.getOffset());
    }

    @Nonnull
    private TextHighlighter getTextHighlighter() {
        if (textHighlighter == null) {
            onSharedPreferenceChanged(preferences, theme.getKey());
        }
        return textHighlighter;
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
            textHighlighter = new TextHighlighter(color, true, engine);
        }
    }

    private int getTextColor(@Nonnull SharedPreferences preferences) {
        final Preferences.Gui.Theme theme = Preferences.Gui.getTheme(preferences);
        final Application application = App.getApplication();
        return theme.getTextColorFor(application).normal;
    }
}
