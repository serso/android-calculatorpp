/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator.plot;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 1/18/13
 * Time: 8:58 PM
 */
public class GraphCalculatorImpl extends AbstractGraphCalculator {

    @Override
    protected void compute(@Nonnull XyFunction f,
                           float xMin,
                           float xMax,
                           float yMin,
                           float yMax,
                           @Nonnull GraphData graph,
                           @Nonnull Graph2dDimensions dimensions) {
        graph.push(xMin, (float) f.eval(xMin));

        final float xScale = dimensions.getXGraphToViewScale();
        final float yScale = dimensions.getYGraphToViewScale();
        final float maxStep = 15.8976f * xScale;
        final float minStep = .05f * xScale;

        float yTheta = yScale;
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
