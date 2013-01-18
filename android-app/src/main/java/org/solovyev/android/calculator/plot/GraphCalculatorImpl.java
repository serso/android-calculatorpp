package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 8:58 PM
 */
public class GraphCalculatorImpl extends AbstractGraphCalculator {

    @Override
    protected void compute(@NotNull XyFunction f,
                             float xMin,
                             float xMax,
                             float yMin,
                             float yMax,
                             @NotNull GraphData graph,
                             @NotNull Graph2dDimensions dimensions) {
        graph.push(xMin, (float)f.eval(xMin));

        final float ratio = dimensions.getGraphToViewRatio();
        final float maxStep = 15.8976f * ratio;
        final float minStep = .05f * ratio;

        float yTheta = ratio;
        yTheta = yTheta * yTheta;


        float leftX;
        float leftY;

        float rightX = graph.getLastX();
        float rightY = graph.getLastY();

        while (true) {
            leftX = rightX;
            leftY = rightY;

            if (leftX > xMax) {
                break;
            }

            if (next.empty()) {
                float x = leftX + maxStep;
                next.push(x, (float) f.eval(x));
            }

            rightX = next.getLastX();
            rightY = next.getLastY();
            next.pop();

            if (Float.isNaN(leftY) || Float.isNaN(rightY)) {
                continue;
            }

            float dx = rightX - leftX;
            float middleX = (leftX + rightX) / 2;
            float middleY = (float) f.eval(middleX);

            boolean middleIsOutside = (middleY < leftY && middleY < rightY) || (leftY < middleY && rightY < middleY);

            if (dx < minStep) {
                // Calculator.log("minStep");
                if (middleIsOutside) {
                    graph.push(rightX, Float.NaN);
                }
                graph.push(rightX, rightY);
                continue;
            }

            if (middleIsOutside && ((leftY < yMin && rightY > yMax) || (leftY > yMax && rightY < yMin))) {
                graph.push(rightX, Float.NaN);
                graph.push(rightX, rightY);
                // Calculator.log("+-inf");
                continue;
            }

            if (!middleIsOutside) {
                if (distance2(leftX, leftY, rightX, rightY, middleY) < yTheta) {
                    graph.push(rightX, rightY);
                    continue;
                }
            }

            next.push(rightX, rightY);
            next.push(middleX, middleY);
            rightX = leftX;
            rightY = leftY;
        }
    }

    // distance as above when x==(x1+x2)/2.
    private float distance2(float x1, float y1, float x2, float y2, float y) {
        final float dx = x2 - x1;
        final float dy = y2 - y1;
        final float up = dx * (y1 + y2 - y - y);
        return up * up / (dx * dx + dy * dy);
    }
}
