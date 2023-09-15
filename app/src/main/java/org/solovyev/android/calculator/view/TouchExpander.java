package org.solovyev.android.calculator.view;

import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewTreeObserver;

public class TouchExpander implements ViewTreeObserver.OnGlobalLayoutListener {

    @NonNull
    private final View mView;
    @NonNull
    private final Rect mExtra;
    // non-null when a TouchDelegate has been set
    @Nullable
    private View mParent;

    private TouchExpander(@NonNull View view, @Px int extra) {
        this(view, new Rect(extra, extra, extra, extra));
    }

    private TouchExpander(@NonNull View view, @NonNull Rect extra) {
        mView = view;
        mExtra = extra;

        attach();
    }

    private static void outset(@NonNull Rect rect, @NonNull Rect diff) {
        rect.left -= diff.left;
        rect.top -= diff.top;
        rect.right += diff.right;
        rect.bottom += diff.bottom;
    }

    @NonNull
    public static TouchExpander attach(@NonNull View view, @Px int extra) {
        return new TouchExpander(view, extra);
    }

    @NonNull
    public static TouchExpander attach(@NonNull View view, @NonNull Rect extra) {
        return new TouchExpander(view, extra);
    }

    private void attach() {
        if (ViewCompat.isLaidOut(mView)) {
            onLaidOut();
            return;
        }
        mView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (!ViewCompat.isLaidOut(mView)) return;
        mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        onLaidOut();
    }

    private void onLaidOut() {
        if (!ViewCompat.isLaidOut(mView)) {
            throw new IllegalStateException("View is not laid out");
        }
        final Rect hitRect = new Rect();
        mView.getHitRect(hitRect);
        outset(hitRect, mExtra);

        mParent = (View) mView.getParent();
        mParent.setTouchDelegate(new TouchDelegate(hitRect, mView));
    }

    public void detach() {
        mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (mParent != null) {
            mParent.setTouchDelegate(null);
            mParent = null;
        }
    }
}
