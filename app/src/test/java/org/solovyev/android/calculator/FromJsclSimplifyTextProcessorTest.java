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

import org.junit.Test;
import org.solovyev.android.calculator.text.FromJsclSimplifyTextProcessor;
import org.solovyev.android.calculator.variables.CppVariable;

import static org.junit.Assert.assertEquals;

public class FromJsclSimplifyTextProcessorTest {

    @Test
    public void testProcess() throws Exception {
        final Engine engine = Tests.makeEngine();
        FromJsclSimplifyTextProcessor tp = new FromJsclSimplifyTextProcessor(engine);
        //Assert.assertEquals("(e)", tp.process("(2.718281828459045)"));
        //Assert.assertEquals("ee", tp.process("2.718281828459045*2.718281828459045"));
        //Assert.assertEquals("((e)(e))", tp.process("((2.718281828459045)*(2.718281828459045))"));
        engine.getMathEngine().setGroupingSeparator(' ');
        //Assert.assertEquals("123 456 789e", tp.process("123456789*2.718281828459045"));
        //Assert.assertEquals("123 456 789e", tp.process("123 456 789 * 2.718281828459045"));
        //Assert.assertEquals("t11e", tp.process("t11*2.718281828459045"));
        //Assert.assertEquals("e", tp.process("2.718281828459045"));
        //Assert.assertEquals("tee", tp.process("t2.718281828459045*2.718281828459045"));

        engine.getVariablesRegistry().addOrUpdate(CppVariable.builder("t2.718281828459045", 2).build().toJsclConstant());
        engine.getVariablesRegistry().addOrUpdate(CppVariable.builder("t").build().toJsclConstant());
        //Assert.assertEquals("t2.718281828459045e", tp.process("t2.718281828459045*2.718281828459045"));
        //Assert.assertEquals("ee", tp.process("2.718281828459045*2.718281828459045"));
        assertEquals("t×", tp.process("t*"));
        assertEquals("×t", tp.process("*t"));
        assertEquals("t2", tp.process("t*2"));
        assertEquals("2t", tp.process("2*t"));
        engine.getVariablesRegistry().addOrUpdate(CppVariable.builder("t").build().toJsclConstant());
        assertEquals("t×", tp.process("t*"));
        assertEquals("×t", tp.process("*t"));

        assertEquals("t2", tp.process("t*2"));
        assertEquals("2t", tp.process("2*t"));

        assertEquals("t^2×2", tp.process("t^2*2"));
        assertEquals("2t^2", tp.process("2*t^2"));

        assertEquals("t^[2×2t]", tp.process("t^[2*2*t]"));
        assertEquals("2t^2[2t]", tp.process("2*t^2[2*t]"));

        engine.getVariablesRegistry().addOrUpdate(CppVariable.builder("k").build().toJsclConstant());
        assertEquals("(t+2k)[k+2t]", tp.process("(t+2*k)*[k+2*t]"));
        assertEquals("(te+2k)e[k+2te]", tp.process("(t*e+2*k)*e*[k+2*t*e]"));


        assertEquals("tlog(3)", tp.process("t*log(3)"));
        assertEquals("t√(3)", tp.process("t*√(3)"));
        assertEquals("20x", tp.process("20*x"));
        assertEquals("20x", tp.process("20x"));
        assertEquals("2×0x3", tp.process("2*0x3"));
        assertEquals("2×0x:3", tp.process("2*0x:3"));
    }
}
