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

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.solovyev.android.calculator.variables.OldVar;
import org.solovyev.android.calculator.variables.OldVars;

import java.io.StringWriter;

import jscl.math.function.IConstant;

import static org.junit.Assert.assertEquals;

/**
 * User: serso
 * Date: 11/7/11
 * Time: 7:52 PM
 */
public class OldVarTest {

    private static final String xml = "<vars>\n" +
            "   <vars class=\"java.util.ArrayList\">\n" +
            "      <var>\n" +
            "         <name>e</name>\n" +
            "         <value>2.718281828459045</value>\n" +
            "         <system>true</system>\n" +
            "         <description>description</description>\n" +
            "      </var>\n" +
            "      <var>\n" +
            "         <name>;</name>\n" +
            "         <value>3.0</value>\n" +
            "         <system>true</system>\n" +
            "      </var>\n" +
            "   </vars>\n" +
            "</vars>";

    @Test
    public void testXml() throws Exception {
        final OldVars vars = new OldVars();
        OldVar first = new OldVar.Builder("e", Math.E).setDescription("description").setSystem(true).create();
        vars.list.add(first);
        OldVar second = new OldVar.Builder(";", 3d).setSystem(true).create();
        vars.list.add(second);

        final StringWriter sw = new StringWriter();
        final Serializer serializer = new Persister();
        serializer.write(vars, sw);

        assertEquals(xml, sw.toString());

        final OldVars result = serializer.read(OldVars.class, xml);
        final IConstant actualFirst = result.list.get(0);
        final IConstant actualSecond = result.list.get(1);

        areEqual(first, actualFirst);
        areEqual(second, actualSecond);

    }

    private void areEqual(IConstant expected, IConstant actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getValue(), actual.getValue());
    }
}
