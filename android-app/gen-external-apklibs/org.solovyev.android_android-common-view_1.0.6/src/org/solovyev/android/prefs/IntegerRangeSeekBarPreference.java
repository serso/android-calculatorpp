/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.prefs;

import android.content.Context;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.text.NumberIntervalMapper;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 10:04 PM
 */
public class IntegerRangeSeekBarPreference extends RangeSeekBarPreference<Integer> {

	public IntegerRangeSeekBarPreference(@NotNull Context context, AttributeSet attrs) {
		super(context, attrs, new NumberIntervalMapper<Integer>(Integer.class));
	}

    @NotNull
    @Override
    protected Integer getDefaultStep() {
        return 1;
    }

    @NotNull
    @Override
    protected Integer add(@NotNull Integer l, @NotNull Integer r) {
        return l + r;
    }
}
