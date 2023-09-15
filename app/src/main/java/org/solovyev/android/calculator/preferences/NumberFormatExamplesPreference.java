package org.solovyev.android.calculator.preferences;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import jscl.JsclMathEngine;

@SuppressWarnings("unused")
public class NumberFormatExamplesPreference extends Preference {
    public NumberFormatExamplesPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NumberFormatExamplesPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NumberFormatExamplesPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberFormatExamplesPreference(Context context) {
        super(context);
    }

    public void update(JsclMathEngine engine) {
        final StringBuilder examples = new StringBuilder();
        examples.append("     1/3 = ").append(engine.format(1d / 3)).append("\n");
        examples.append("      √2 = ").append(engine.format(Math.sqrt(2d))).append("\n");
        examples.append("\n");
        examples.append("    1000 = ").append(engine.format(1000d)).append("\n");
        examples.append(" 1000000 = ").append(engine.format(1000000d)).append("\n");
        examples.append("   11^10 = ").append(engine.format(Math.pow(11d, 10))).append("\n");
        examples.append("   10^24 = ").append(engine.format(Math.pow(10d, 24))).append("\n");
        examples.append("\n");
        examples.append("   0.001 = ").append(engine.format(0.001d)).append("\n");
        examples.append("0.000001 = ").append(engine.format(0.000001d)).append("\n");
        examples.append("  11^−10 = ").append(engine.format(Math.pow(11d, -10))).append("\n");
        examples.append("  10^−24 = ").append(engine.format(Math.pow(10d, -24)));
        setSummary(examples);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final View summary = holder.findViewById(android.R.id.summary);
        if (summary instanceof TextView) {
            final TextView textView = (TextView) summary;
            textView.setMaxLines(12);
            textView.setLines(12);
            textView.setTypeface(Typeface.MONOSPACE);
        }
    }
}
