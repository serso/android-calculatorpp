package org.solovyev.android.calculator.plot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class CalculatorPlotFunctionsController {

	@NotNull
	private static final CalculatorPlotFunctionsController instance = new CalculatorPlotFunctionsController();

	@NotNull
	private final List<XyFunction> functions = new ArrayList<XyFunction>();

	private CalculatorPlotFunctionsController() {
	}

	@NotNull
	public static CalculatorPlotFunctionsController getInstance() {
		return instance;
	}

	@NotNull
	public List<XyFunction> getFunctions() {
		return Collections.unmodifiableList(functions);
	}

	public boolean addFunction(@NotNull XyFunction function) {
		if (!functions.contains(function)) {
			return functions.add(function);
		} else {
			return false;
		}
	}
}
