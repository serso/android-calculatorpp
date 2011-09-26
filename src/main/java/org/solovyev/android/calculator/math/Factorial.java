package org.solovyev.android.calculator.math;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.math.function.Function;

/**
 * User: serso
 * Date: 9/26/11
 * Time: 12:58 PM
 */
public class Factorial extends Function {

	public Factorial(Generic[] parameter) {
		super("fact", parameter);
	}

	@Override
	public Generic evaluate() {
        return expressionValue();
	}

	@Override
	public Generic evalelem() {
		return null;
	}

	@Override
	public Generic evalsimp() {
		return null;
	}

	@Override
	public Generic evalnum() {
		return null;
	}

	@Override
	public Generic antiderivative(int n) throws NotIntegrableException {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	public Generic derivative(int n) {
		throw new UnsupportedOperationException("Not implemented yet!");
	}

	@Override
	protected Variable newinstance() {
		return new Factorial(null);
	}
}
