package org.solovyev.android.view;

import android.graphics.Canvas;
import android.view.View;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/19/12
 * Time: 12:55 AM
 */
public interface ViewGrayable extends Grayable {

    void dispatchDraw(@NotNull View view, @NotNull Canvas canvas);
}
