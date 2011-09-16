/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.android.view;

import android.widget.TextView;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/10/11
 * Time: 7:21 PM
 */
public interface FontSizeAdjuster {

    void adjustFontSize(@NotNull TextView textView);
}
