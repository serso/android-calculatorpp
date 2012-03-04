package org.solovyev.android.calculator.model;

import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import jscl.math.function.IConstant;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 12/22/11
 * Time: 5:15 PM
 */
@Root
public class Functions implements MathEntityPersistenceContainer<AFunction> {

	@ElementList(type = CustomFunction.class)
	private List<AFunction> functions = new ArrayList<AFunction>();

	public Functions() {
	}

	public List<AFunction> getEntities() {
		return functions;
	}
}
