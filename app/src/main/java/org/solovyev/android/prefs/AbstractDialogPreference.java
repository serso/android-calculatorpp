/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.solovyev.common.Objects;
import org.solovyev.common.text.Mapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base class for creating preferences with dialogs
 *
 * @param <T>
 */
public abstract class AbstractDialogPreference<T> extends DialogPreference {

    @Nonnull
    protected final static String localNameSpace = "http://schemas.android.com/apk/res-auto";

    @Nonnull
    protected final static String androidns = "http://schemas.android.com/apk/res/android";

    @Nonnull
    private static final String TAG = AbstractDialogPreference.class.getSimpleName();
    @Nonnull
    private final Context context;
    @Nullable
    private final String defaultStringValue;
    private final boolean needValueText;
    @Nonnull
    private final Mapper<T> mapper;
    @Nullable
    private TextView valueTextView;
    @Nullable
    private String valueText;
    @Nullable
    private View preferenceView;
    @Nullable
    private String description;
    @Nullable
    private T value;
    @Nullable
    private T defaultValue;

    public AbstractDialogPreference(Context context, AttributeSet attrs, @Nullable String defaultStringValue, boolean needValueText, @Nonnull Mapper<T> mapper) {
        super(context, attrs);
        this.context = context;
        this.defaultStringValue = defaultStringValue;
        this.needValueText = needValueText;
        this.mapper = mapper;

        final String defaultValueFromAttrs = attrs.getAttributeValue(androidns, "defaultValue");
        if (defaultValueFromAttrs != null) {
            defaultValue = getMapper().parseValue(defaultValueFromAttrs);
        } else if (defaultStringValue != null) {
            defaultValue = getMapper().parseValue(defaultStringValue);
        } else {
            throw new IllegalArgumentException();
        }

        description = attrs.getAttributeValue(androidns, "dialogMessage");
        valueText = attrs.getAttributeValue(androidns, "text");
    }

    @Nullable
    protected View getPreferenceView() {
        return preferenceView;
    }

    @Nullable
    public T getValue() {
        return value;
    }

    public void setValue(@Nullable T value) {
        this.value = value;
    }

    @Override
    @Nonnull
    protected final LinearLayout onCreateDialogView() {
        if (shouldPersist()) {
            value = getPersistedValue();
        }

        final LinearLayout result = new LinearLayout(context);
        result.setOrientation(LinearLayout.VERTICAL);
        result.setPadding(6, 6, 6, 6);

        if (description != null) {
            final TextView splashText = new TextView(context);
            splashText.setText(description);
            result.addView(splashText);
        }

        if (needValueText) {
            valueTextView = new TextView(context);
            valueTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            valueTextView.setTextSize(32);

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            result.addView(valueTextView, params);
        }

        preferenceView = createPreferenceView(context);
        initPreferenceView(preferenceView, value);

        final LinearLayout.LayoutParams params = getParams();
        if (params != null) {
            result.addView(preferenceView, params);
        } else {
            result.addView(preferenceView);
        }

        return result;
    }

    @Nullable
    protected abstract LinearLayout.LayoutParams getParams();

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        super.onSetInitialValue(restore, defaultValue);

        if (restore) {
            if (shouldPersist()) {
                value = getPersistedValue();
            } else {
                value = this.defaultValue;
            }
        } else {
            value = (T) defaultValue;
            if (shouldPersist()) {
                persist(this.value);
            }
        }
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        if (this.preferenceView != null) {
            initPreferenceView(this.preferenceView, value);
        }
    }

    /**
     * Creates view which is responsible for changing preference value (for example, Spinner or EditText)
     *
     * @param context context
     * @return view which changes the preference value
     */
    @Nonnull
    protected abstract View createPreferenceView(@Nonnull Context context);

    /**
     * @param v     view to be filled with initial data (the one which has been created with {@link #createPreferenceView} method)
     * @param value current preference value
     */
    protected abstract void initPreferenceView(@Nonnull View v, @Nullable T value);

    @Nullable
    private T getPersistedValue() {
        String persistedString = getPersistedString(defaultStringValue);

        if (Objects.areEqual(persistedString, defaultStringValue)) {
            return defaultValue;
        } else {
            return getMapper().parseValue(persistedString);
        }
    }

    protected void persistValue(@Nullable T value) {
        this.value = value;
        if (!callChangeListener(value)) {
            return;
        }
        if (!shouldPersist()) {
            return;
        }
        persist(value);
    }

    private void persist(@Nullable T value) {
        if (value != null) {
            final String toBePersistedString = getMapper().formatValue(value);
            if (toBePersistedString != null) {
                if (callChangeListener(toBePersistedString)) {
                    persistString(toBePersistedString);
                }
            }
        }
    }

    @Nullable
    public String getValueText() {
        return valueText;
    }

    protected void updateValueText(@Nonnull String text) {
        if (valueTextView != null) {
            valueTextView.setText(text);
        }
    }

    @Nonnull
    private Mapper<T> getMapper() {
        return this.mapper;
    }
}
