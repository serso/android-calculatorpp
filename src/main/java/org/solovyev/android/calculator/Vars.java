package org.solovyev.android.calculator;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * User: serso
 * Date: 9/29/11
 * Time: 5:19 PM
 */

@Root
public class Vars {

	@ElementList
	private List<Var> vars = new ArrayList<Var>();

	public Vars() {
	}

	public List<Var> getVars() {
		return vars;
	}
}
