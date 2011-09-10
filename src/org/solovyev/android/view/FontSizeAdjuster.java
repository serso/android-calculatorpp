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
