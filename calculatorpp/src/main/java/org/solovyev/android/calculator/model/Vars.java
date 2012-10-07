package org.solovyev.android.calculator.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.solovyev.android.calculator.MathEntityPersistenceContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 5:19 PM
 */

@Root
public class Vars implements MathEntityPersistenceContainer<Var> {

	@ElementList(type = Var.class)
	private List<Var> vars = new ArrayList<Var>();

	public Vars() {
	}

	public List<Var> getEntities() {
		return vars;
	}
}
