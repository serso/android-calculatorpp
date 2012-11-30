package org.solovyev.android.view.scroll;

import android.os.Bundle;
import android.widget.ScrollView;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * User: serso
 * Date: 8/5/12
 * Time: 2:09 AM
 */
public class ScrollViewState implements Serializable {

    @NotNull
    private static final String SCROLL_VIEW_STATE = "scroll_view_state";

    private int scrollX = 0;

    private int scrollY = 0;

    public ScrollViewState() {
    }

    public ScrollViewState(@NotNull ScrollView scrollView) {
        this.scrollX = scrollView.getScrollX();
        this.scrollY = scrollView.getScrollY();
    }

    public void restoreState(@NotNull final ScrollView scrollView) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(scrollX, scrollY);
            }
        });

    }

    public static void saveState(@NotNull Bundle out, @NotNull final ScrollView scrollView) {
        out.putSerializable(SCROLL_VIEW_STATE, new ScrollViewState(scrollView));
    }

    public static void restoreState(@NotNull Bundle in, @NotNull final ScrollView scrollView) {
        final Object o = in.getSerializable(SCROLL_VIEW_STATE);
        if (o instanceof ScrollViewState) {
            ((ScrollViewState) o).restoreState(scrollView);
        }
    }
}
