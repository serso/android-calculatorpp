package org.solovyev.android.calculator.function;

import jscl.CustomFunctionCalculationException;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.model.AFunction;
import org.solovyev.android.calculator.model.MathEntityBuilder;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:21 PM
 */
public final class FunctionBuilderAdapter implements MathEntityBuilder<Function> {

	@NotNull
	private final AFunction.Builder nestedBuilder;

	public FunctionBuilderAdapter(@NotNull AFunction.Builder nestedBuilder) {
		this.nestedBuilder = nestedBuilder;
	}

	@NotNull
	@Override
	public MathEntityBuilder<Function> setName(@NotNull String name) {
		nestedBuilder.setName(name);
		return this;
	}

	@NotNull
	@Override
	public MathEntityBuilder<Function> setDescription(@Nullable String description) {
		nestedBuilder.setDescription(description);
		return this;
	}

	@NotNull
	@Override
	public MathEntityBuilder<Function> setValue(@Nullable String value) {
		nestedBuilder.setValue(value);
		return this;
	}

	@NotNull
	@Override
	public Function create() throws CustomFunctionCalculationException, AFunction.Builder.CreationException {
		final AFunction function = nestedBuilder.create();
		return new CustomFunction.Builder(function).create();
	}
}
