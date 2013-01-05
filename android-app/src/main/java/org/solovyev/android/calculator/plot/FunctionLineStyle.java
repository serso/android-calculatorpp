package org.solovyev.android.calculator.plot;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/5/13
 * Time: 7:37 PM
 */
public enum FunctionLineStyle {

    solid {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(null);
        }
    },

    dashed {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(new DashPathEffect(new float[] {10, 20}, 0));
        }
    },

    dotted {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(new DashPathEffect(new float[] {5, 1}, 0));
        }
    },

    dash_dotted {
        @Override
        public void applyToPaint(@NotNull Paint paint) {
            paint.setPathEffect(new DashPathEffect(new float[] {10, 20, 5, 1}, 0));
        }
    };

    public abstract void applyToPaint(@NotNull Paint paint);

}
