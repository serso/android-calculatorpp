package org.solovyev.android.views.dragbutton;

import android.support.annotation.Nullable;

public interface DragView {
    void setOnDragListener(@Nullable DragListener listener);

    void setVibrateOnDrag(boolean vibrateOnDrag);
}
