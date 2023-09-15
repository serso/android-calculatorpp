package org.solovyev.android.views.dragbutton;

import androidx.annotation.Nullable;

public interface DragView {
    int getId();
    void setOnDragListener(@Nullable DragListener listener);
    void setVibrateOnDrag(boolean vibrateOnDrag);
    void setHighContrast(boolean highContrast);
}
