package org.solovyev.android.view.drag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 11/3/12
 * Time: 1:51 PM
 */
public interface DirectionDragButtonDef extends DragButtonDef {

    @Nullable
    CharSequence getText(@NotNull DragDirection dragDirection);
}
