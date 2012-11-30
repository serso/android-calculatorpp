package org.solovyev.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 4/19/12
 * Time: 12:34 AM
 */
public class GrayableRelativeLayout extends RelativeLayout implements Grayable {

    @NotNull
    private ViewGrayable grayable = new GrayableImpl();

    public GrayableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GrayableRelativeLayout(Context context) {
        super(context);
    }

    @Override
    public void grayOut() {
        grayable.grayOut();
    }

    @Override
    public void grayIn() {
        grayable.grayIn();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        this.grayable.dispatchDraw(this, canvas);
    }
}

