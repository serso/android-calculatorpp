package org.solovyev.android.calculator.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class PagerViewFabBehavior extends FloatingActionButton.Behavior {

    public PagerViewFabBehavior() {
        super();
    }

    public PagerViewFabBehavior(Context context, AttributeSet attributeSet) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
            FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        switch (nestedScrollAxes) {
            case ViewCompat.SCROLL_AXIS_HORIZONTAL:
                return target instanceof ViewPager;
            case ViewCompat.SCROLL_AXIS_VERTICAL:
                return target instanceof RecyclerView;
            default:
                return false;
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
            View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onScroll(child, dxConsumed, dyConsumed);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
            View target, float velocityX, float velocityY, boolean consumed) {
        return onScroll(child, velocityX, velocityY);
    }

    private boolean onScroll(FloatingActionButton child, float scrollX, float scrollY) {
        if (scrollY > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide();
            return true;
        } else if (scrollY < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
            return true;
        } else if (scrollX != 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
            return true;
        }
        return false;
    }
}
