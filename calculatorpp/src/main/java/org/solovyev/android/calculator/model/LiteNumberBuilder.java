/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.model;

import jscl.MathEngine;
import jscl.NumeralBase;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.math.MathType;

/**
 * User: serso
 * Date: 12/15/11
 * Time: 8:33 PM
 */

public class LiteNumberBuilder extends AbstractNumberBuilder {

	public LiteNumberBuilder(@NotNull MathEngine engine) {
		super(engine);
		this.nb = engine.getNumeralBase();
	}

	public void process(@NotNull MathType.Result mathTypeResult) {
		if (canContinue(mathTypeResult)) {
			// let's continue building number
			if (numberBuilder == null) {
				// if new number => create new builder
				numberBuilder = new StringBuilder();
			}

			if (mathTypeResult.getMathType() != MathType.numeral_base) {
				// just add matching string
				numberBuilder.append(mathTypeResult.getMatch());
			} else {
				// set explicitly numeral base (do not include it into number)
				nb = NumeralBase.getByPrefix(mathTypeResult.getMatch());
			}

		} else {
			// process current number (and go to the next one)
			if (numberBuilder != null) {
				numberBuilder = null;

				// must set default numeral base (exit numeral base mode)
				nb = engine.getNumeralBase();
			}
		}
	}

}

