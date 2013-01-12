package org.solovyev.android.calculator.plot;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 7:37 PM
 */
public enum AndroidPlotLineStyle {

    solid(PlotLineStyle.solid) {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(null);
        }
    },

    dashed(PlotLineStyle.dashed) {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        }
    },

    dotted(PlotLineStyle.dotted) {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(new DashPathEffect(new float[]{5, 1}, 0));
        }
    },

    dash_dotted(PlotLineStyle.dash_dotted) {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(new DashPathEffect(new float[]{10, 20, 5, 1}, 0));
        }
    };

    @NotNull
    private final PlotLineStyle plotLineStyle;

    AndroidPlotLineStyle(@NotNull PlotLineStyle plotLineStyle) {
        this.plotLineStyle = plotLineStyle;
    }

    public abstract void applyToPaint(@NotNull Paint paint);

    @Nullable
    public static AndroidPlotLineStyle valueOf(@NotNull PlotLineStyle plotLineStyle) {
        for (AndroidPlotLineStyle androidPlotLineStyle : values()) {
            if ( androidPlotLineStyle.plotLineStyle == plotLineStyle ) {
                return androidPlotLineStyle;
            }
        }

        return null;
    }

}
