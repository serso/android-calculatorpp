package org.solovyev.android.views.dragbutton;

import android.support.annotation.NonNull;

public interface DirectionDragView extends DragView {
    @NonNull
    DirectionText getText(@NonNull DragDirection direction);
}
