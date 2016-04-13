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

import android.graphics.Color;
import android.os.Build;
import jscl.MathEngine;
import jscl.NumeralBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.solovyev.android.calculator.text.TextProcessor;
import org.solovyev.android.calculator.view.TextHighlighter;

import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(value = RobolectricGradleTestRunner.class)
public class TextHighlighterTest {

    private Engine engine;

    @Before
    public void setUp() throws Exception {
        engine = Tests.makeEngine();
    }

    @Test
    public void testProcess() throws Exception {
        TextProcessor<?, String> textHighlighter = new TextHighlighter(Color.TRANSPARENT, false, engine);

        final Random random = new Random(new Date().getTime());
        for (int i = 0; i < 1000; i++) {
            final StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 1000; j++) {
                sb.append(random.nextBoolean() ? "(" : ")");
            }
            try {
                textHighlighter.process(sb.toString());
            } catch (Exception e) {
                System.out.println(sb.toString());
                throw e;
            }
        }

        assertEquals("<font color=\"#000000\"></font>)(((())())", textHighlighter.process(")(((())())").toString());
        assertEquals(")", textHighlighter.process(")").toString());
        assertEquals(")()(", textHighlighter.process(")()(").toString());

        textHighlighter = new TextHighlighter(0, true, engine);
        assertEquals("1 000 000", textHighlighter.process("1000000").toString());
        assertEquals("1 000 000", textHighlighter.process("1000000").toString());
        assertEquals("0.1E3", textHighlighter.process("0.1E3").toString());
        assertEquals("1E3", textHighlighter.process("1E3").toString());
        assertEquals("2<b>0x:</b>", textHighlighter.process("20x:").toString());
        assertEquals("20g", textHighlighter.process("20g").toString());
        assertEquals("22g", textHighlighter.process("22g").toString());
        assertEquals("20ю", textHighlighter.process("20ю").toString());
        assertEquals("20ъ", textHighlighter.process("20ъ").toString());
        assertEquals("3!!", textHighlighter.process("3!!").toString());
        assertEquals("2", textHighlighter.process("2").toString());
        assertEquals("21", textHighlighter.process("21").toString());
        assertEquals("214", textHighlighter.process("214").toString());
        assertEquals("2 145", textHighlighter.process("2 145").toString());
        assertEquals("1 000 000E3", textHighlighter.process("1000000E3").toString());
        assertEquals("-1 000 000E3", textHighlighter.process("-1000000E3").toString());
        assertEquals("-1 000 000E-3", textHighlighter.process("-1000000E-3").toString());
        assertEquals("-1 000 000E-30000", textHighlighter.process("-1000000E-30000").toString());
        textHighlighter = new TextHighlighter(0, false, engine);

        textHighlighter.process("cannot calculate 3^10^10 !!!\n" +
                "        unable to enter 0. FIXED\n" +
                "        empty display in Xperia Rayo\n" +
                "        check привиденная FIXED\n" +
                "        set display result only if text in editor was not changed FIXED\n" +
                "        shift M text to the left\n" +
                "        do not show SYNTAX ERROR always (may be create send clock?q) FIXED\n" +
                "        ln(8)*log(8) =>  ln(8)*og(8) FIXED\n" +
                "        copy/paste ln(8)*log(8)\n" +
                "        6!^2 ERROR");

        assertEquals("<font color=\"#000000\"><i>sin</i>(</font><font color=\"#ffff9a\">2</font><font color=\"#000000\">)</font>", textHighlighter.process("sin(2)").toString());
        assertEquals("<font color=\"#000000\"><i>atanh</i>(</font><font color=\"#ffff9a\">2</font><font color=\"#000000\">)</font>", textHighlighter.process("atanh(2)").toString());


        assertEquals("<b>0x:</b>E", textHighlighter.process("0x:E").toString());
        assertEquals("<b>0x:</b>6F", textHighlighter.process("0x:6F").toString());
        assertEquals("<b>0x:</b>6F.", textHighlighter.process("0x:6F.").toString());
        assertEquals("<b>0x:</b>6F.2", textHighlighter.process("0x:6F.2").toString());
        assertEquals("<b>0x:</b>6F.B", textHighlighter.process("0x:6F.B").toString());
        assertEquals("<b>0x:</b>006F.B", textHighlighter.process("0x:006F.B").toString());
        assertEquals("<b>0x:</b>0", textHighlighter.process("0x:0").toString());
        assertEquals("<b>0x:</b>FF33233FFE", textHighlighter.process("0x:FF33233FFE").toString());
        assertEquals("<b>0x:</b>FF33 233 FFE", textHighlighter.process("0x:FF33 233 FFE").toString());

        final MathEngine me = engine.getMathEngine();
        try {
            me.setNumeralBase(NumeralBase.hex);
            assertEquals("E", textHighlighter.process("E").toString());
            assertEquals(".E", textHighlighter.process(".E").toString());
            assertEquals("E+", textHighlighter.process("E+").toString());
            assertEquals("E.", textHighlighter.process("E.").toString());
            assertEquals(".E.", textHighlighter.process(".E.").toString());
            assertEquals("6F", textHighlighter.process("6F").toString());
            assertEquals("6F", textHighlighter.process("6F").toString());
            assertEquals("6F.", textHighlighter.process("6F.").toString());
            assertEquals("6F.2", textHighlighter.process("6F.2").toString());
            assertEquals("6F.B", textHighlighter.process("6F.B").toString());
            assertEquals("006F.B", textHighlighter.process("006F.B").toString());
        } finally {
            me.setNumeralBase(NumeralBase.dec);
        }

        assertEquals("<b>0b:</b>110101", textHighlighter.process("0b:110101").toString());
        assertEquals("<b>0b:</b>110101.", textHighlighter.process("0b:110101.").toString());
        assertEquals("<b>0b:</b>110101.101", textHighlighter.process("0b:110101.101").toString());
        assertEquals("<b>0b:</b>11010100.1", textHighlighter.process("0b:11010100.1").toString());
        assertEquals("<b>0b:</b>110101.0", textHighlighter.process("0b:110101.0").toString());
        assertEquals("<b>0b:</b>0", textHighlighter.process("0b:0").toString());
        assertEquals("<b>0b:</b>1010100101111010101001", textHighlighter.process("0b:1010100101111010101001").toString());
        assertEquals("<b>0b:</b>101 010   01 0 111   1 0 10101001", textHighlighter.process("0b:101 010   01 0 111   1 0 10101001").toString());

        try {
            me.setNumeralBase(NumeralBase.bin);
            assertEquals("110101", textHighlighter.process("110101").toString());
            assertEquals("110101.", textHighlighter.process("110101.").toString());
            assertEquals("110101.101", textHighlighter.process("110101.101").toString());
            assertEquals("11010100.1", textHighlighter.process("11010100.1").toString());
            assertEquals("110101.0", textHighlighter.process("110101.0").toString());
            assertEquals("0", textHighlighter.process("0").toString());
            assertEquals("1010100101111010101001", textHighlighter.process("1010100101111010101001").toString());
            assertEquals("101 010   01 0 111   1 0 10101001", textHighlighter.process("101 010   01 0 111   1 0 10101001").toString());
        } finally {
            me.setNumeralBase(NumeralBase.dec);
        }
    }

    @Test
    public void testTime() throws Exception {
        final TextProcessor<?, String> textHighlighter = new TextHighlighter(Color.WHITE, false, engine);

        final int count = 1000;
        final String subExpression = "cos(acos(t8ln(t5t85tln(8ln(5t55tln(5))))))+tln(88cos(tln(t)))+t√(ln(t))";
        final StringBuilder expression = new StringBuilder(subExpression.length() * count);
        for (int i = 0; i < count; i++) {
            expression.append(subExpression);
            expression.append("+");
        }
        expression.append(subExpression);

        long startTime = System.currentTimeMillis();
        textHighlighter.process(expression.toString());
        long endTime = System.currentTimeMillis();
        System.out.println("Total time, ms: " + (endTime - startTime));
    }

    @Test
    public void testDarkColor() throws Exception {
        final TextProcessor<?, String> textHighlighter = new TextHighlighter(Color.BLACK, false, engine);
        assertEquals("", textHighlighter.process("sin(2cos(3))"));
    }

    @Test
    public void testIsDark() throws Exception {
        assertFalse(TextHighlighter.isDark(Color.WHITE));
        assertFalse(TextHighlighter.isDark(Color.LTGRAY));
        assertTrue(TextHighlighter.isDark(Color.DKGRAY));
        assertTrue(TextHighlighter.isDark(Color.BLACK));

    }
}
