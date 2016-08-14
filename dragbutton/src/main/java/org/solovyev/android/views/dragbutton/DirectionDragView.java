package org.solovyev.android.views.dragbutton;

import android.support.annotation.NonNull;

public interface DirectionDragView extends DragView {
    @NonNull
    DirectionTextView.Text getText(@NonNull DragDirection direction);
}
