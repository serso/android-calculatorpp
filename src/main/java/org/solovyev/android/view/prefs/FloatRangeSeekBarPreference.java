/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.view.prefs;

import android.content.Context;
import android.util.AttributeSet;
import org.jetbrains.annotations.NotNull;
import org.solovyev.common.FloatIntervalMapper;
import org.solovyev.common.utils.Interval;
import org.solovyev.common.utils.Mapper;

/**
 * User: serso
 * Date: 9/21/11
 * Time: 11:41 PM
 */
public class FloatRangeSeekBarPreference extends RangeSeekBarPreference<Float> {

	public FloatRangeSeekBarPreference(@NotNull Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@NotNull
	@Override
	protected Mapper<Interval<Float>> getMapper() {
		return new FloatIntervalMapper() ;
	}
}
