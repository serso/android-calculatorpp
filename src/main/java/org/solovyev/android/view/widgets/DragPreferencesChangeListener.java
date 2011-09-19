/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view.widgets;

import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.DragButtonCalibrationActivity;

import java.util.EventListener;

/**
 * User: serso
 * Date: 9/18/11
 * Time: 8:48 PM
 */
public interface DragPreferencesChangeListener extends EventListener{

	void onDragPreferencesChange(@NotNull DragButtonCalibrationActivity.Preferences preferences );
}
