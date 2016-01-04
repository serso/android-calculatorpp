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

package org.solovyev.android.calculator;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: serso
 * Date: 10/20/12
 * Time: 12:31 PM
 */
public class CalculatorEditorViewStateImplTest {

    @Test
    public void testSerialization() throws Exception {
        CalculatorTestUtils.testSerialization(CalculatorEditorViewStateImpl.newDefaultInstance());

        CalculatorEditorViewState out = CalculatorTestUtils.testSerialization(CalculatorEditorViewStateImpl.newInstance("treter", 2));
        Assert.assertEquals(2, out.getSelection());
        Assert.assertEquals("treter", out.getText());
    }
}
