package org.solovyev.android.calculator.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import org.solovyev.android.calculator.CalculatorParseException;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.text.TextProcessorEditorResult;

import javax.annotation.Nonnull;

import static org.solovyev.android.calculator.CalculatorPreferences.Gui.colorDisplay;
import static org.solovyev.android.calculator.view.TextHighlighter.WHITE;

/**
 * User: serso
 * Date: 6/27/13
 * Time: 6:11 PM
 */
public final class EditorTextProcessor implements TextProcessor<TextProcessorEditorResult, String>, SharedPreferences.OnSharedPreferenceChangeListener {

	private boolean highlightText = true;

	private final TextProcessor<TextProcessorEditorResult, String> textHighlighter = new TextHighlighter(WHITE, true);

	public EditorTextProcessor() {
	}

	public void init(@Nonnull Context context) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		preferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(preferences, colorDisplay.getKey());
	}

	@Nonnull
	@Override
	public TextProcessorEditorResult process(@Nonnull String text) throws CalculatorParseException {
		TextProcessorEditorResult result;

		if (highlightText) {

			try {
				final TextProcessorEditorResult processesText = textHighlighter.process(text);

				result = new TextProcessorEditorResult(Html.fromHtml(processesText.toString()), processesText.getOffset());
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

	public boolean isHighlightText() {
		return highlightText;
	}

	public void setHighlightText(boolean highlightText) {
		this.highlightText = highlightText;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
		if (colorDisplay.getKey().equals(key)) {
			this.setHighlightText(colorDisplay.getPreference(preferences));
		}
	}
}
