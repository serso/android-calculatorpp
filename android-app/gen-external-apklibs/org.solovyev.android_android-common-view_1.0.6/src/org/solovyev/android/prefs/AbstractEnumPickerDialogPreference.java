package org.solovyev.android.prefs;

import android.content.Context;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.Labeled;
import org.solovyev.android.LabeledFormatter;
import org.solovyev.android.view.ListRange;
import org.solovyev.android.view.Picker;
import org.solovyev.common.text.EnumMapper;

import java.util.Arrays;

/**
 * User: serso
 * Date: 8/10/12
 * Time: 2:02 AM
 */
public abstract class AbstractEnumPickerDialogPreference<T extends Enum & Labeled> extends AbstractPickerDialogPreference<T> {

    @NotNull
    private final Class<T> enumClass;

    protected AbstractEnumPickerDialogPreference(Context context,
                                                 AttributeSet attrs,
                                                 @Nullable String defaultStringValue,
                                                 boolean needValueText,
                                                 @NotNull Class<T> enumClass) {
        super(context, attrs, defaultStringValue, needValueText, new EnumMapper<T>(enumClass));
        this.enumClass = enumClass;
    }

    @NotNull
    @Override
    protected Picker.Range<T> createRange(@NotNull T selected) {
        return new ListRange<T>(Arrays.asList(enumClass.getEnumConstants()), selected, new LabeledFormatter<T>(getContext()));
    }
}
