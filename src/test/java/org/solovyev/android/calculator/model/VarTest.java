/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import junit.framework.Assert;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.StringWriter;

/**
 * User: serso
 * Date: 11/7/11
 * Time: 7:52 PM
 */
public class VarTest {

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
		final Vars vars = new Vars();
		Var first = new Var.Builder("e", Math.E).setDescription("description").setSystem(true).create();
		vars.getVars().add(first);
		Var second = new Var.Builder(";", 3d).setSystem(true).create();
		vars.getVars().add(second);

		final StringWriter sw = new StringWriter();
		final Serializer serializer = new Persister();
		serializer.write(vars, sw);

		Assert.assertEquals(xml, sw.toString());


		final Vars result = serializer.read(Vars.class, xml);
		final Var actualFirst = result.getVars().get(0);
		final Var actualSecond = result.getVars().get(1);

		areEqual(first, actualFirst);
		areEqual(second, actualSecond);

	}

	private void areEqual(Var expected, Var actual) {
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getDescription(), actual.getDescription());
		Assert.assertEquals(expected.getValue(), actual.getValue());
	}
}
