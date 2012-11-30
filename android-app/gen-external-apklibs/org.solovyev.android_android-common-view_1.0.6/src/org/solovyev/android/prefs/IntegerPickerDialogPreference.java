/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.prefs;

import android.content.Context;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.view.IntegerRange;
import org.solovyev.android.view.NumberRange;
import org.solovyev.common.interval.Interval;
import org.solovyev.common.text.NumberIntervalMapper;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 10:31 PM
 */
public class IntegerPickerDialogPreference extends NumberPickerDialogPreference<Integer>{

    public IntegerPickerDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs, new NumberIntervalMapper<Integer>(Integer.class));
    }

    @NotNull
    @Override
    protected Integer getDefaultStep() {
        return 1;
    }

    @NotNull
    @Override
    protected NumberRange<Integer> createRange(@NotNull Interval<Integer> boundaries, @NotNull Integer step, @NotNull Integer selected) {
        return IntegerRange.newInstance(boundaries.getLeftLimit(), boundaries.getRightLimit(), step, selected);
    }
}
