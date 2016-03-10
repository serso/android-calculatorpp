package org.solovyev.android.views.dragbutton;

import android.support.annotation.NonNull;

import javax.annotation.Nonnull;

public interface DirectionDragView extends DragView {
    @Nonnull
    DirectionTextView.Text getText(@NonNull DragDirection direction);
}
