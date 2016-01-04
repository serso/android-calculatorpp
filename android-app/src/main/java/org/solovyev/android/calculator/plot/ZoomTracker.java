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

class ZoomTracker {

    public static final String TAG = "ZoomTracker";
    private static final float EPS = 1.5f;
    private static final float MIN_DISTANCE = distance(0f, 50f);
    float xValue;
    float yValue;
    private float initialXDistance;
    private float initialYDistance;
    private float initialXValue;
    private float initialYValue;

    private static float distance(float x1, float x2) {
        final float dx = x1 - x2;
        return dx * dx;
    }

    void start(float xValue, float yValue,
               float x1, float y1,
               float x2, float y2) {

        initialXDistance = distance(x1, x2);
        initialYDistance = distance(y1, y2);

        initialXValue = xValue;
        initialYValue = yValue;

        this.xValue = xValue;
        this.yValue = yValue;
    }

    boolean update(float x1, float y1, float x2, float y2) {
        boolean result = false;

        if (initialXDistance > MIN_DISTANCE) {
            final float xDistance = distance(x1, x2);
            if (xDistance > EPS) {
                xValue = initialXDistance / xDistance * initialXValue;
                result = true;
            }
        }

        if (initialYDistance > MIN_DISTANCE) {
            final float yDistance = distance(y1, y2);
            if (yDistance > EPS) {
                yValue = initialYDistance / yDistance * initialYValue;
                result = true;
            }
        }

        return result;
    }
}
