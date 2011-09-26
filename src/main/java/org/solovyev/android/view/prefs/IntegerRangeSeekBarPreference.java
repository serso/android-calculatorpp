/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.content.Context;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.NumberIntervalMapper;
import org.solovyev.common.utils.*;

/**
 * User: serso
 * Date: 9/19/11
 * Time: 10:04 PM
 */
public class IntegerRangeSeekBarPreference extends RangeSeekBarPreference<Integer> {

	public IntegerRangeSeekBarPreference(@NotNull Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@NotNull
	@Override
	protected Mapper<Interval<Integer>> getMapper() {
		return new NumberIntervalMapper<Integer>(Integer.class);
	}

}
