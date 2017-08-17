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

package org.solovyev.android.calculator.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.BaseCalculatorTest;
import org.solovyev.android.calculator.BuildConfig;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class ComparisonTest extends BaseCalculatorTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        engine.getMathEngine().setPrecision(3);
    }

    @Test
    public void testComparisonFunction() throws Exception {
        assertEval("0", "eq(0, 1)");
        assertEval("1", "eq(1, 1)");
        assertEval("1", "eq(1, 1.0)");
        assertEval("0", "eq(1, 1.000000000000001)");
        assertEval("0", "eq(1, 0)");

        assertEval("1", "lt(0, 1)");
        assertEval("0", "lt(1, 1)");
        assertEval("0", "lt(1, 0)");

        assertEval("0", "gt(0, 1)");
        assertEval("0", "gt(1, 1)");
        assertEval("1", "gt(1, 0)");

        assertEval("1", "ne(0, 1)");
        assertEval("0", "ne(1, 1)");
        assertEval("1", "ne(1, 0)");

        assertEval("1", "le(0, 1)");
        assertEval("1", "le(1, 1)");
        assertEval("0", "le(1, 0)");

        assertEval("0", "ge(0, 1)");
        assertEval("1", "ge(1, 1)");
        assertEval("1", "ge(1, 0)");

        assertEval("0", "ap(0, 1)");
        assertEval("1", "ap(1, 1)");
        assertEval("0", "ap(1, 0)");
    }
}
