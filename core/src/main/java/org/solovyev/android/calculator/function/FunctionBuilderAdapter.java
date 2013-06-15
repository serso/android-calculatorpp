package org.solovyev.android.calculator.function;

import jscl.CustomFunctionCalculationException;
import jscl.math.function.CustomFunction;
import jscl.math.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.solovyev.android.calculator.model.AFunction;
import org.solovyev.android.calculator.model.MathEntityBuilder;

/**
 * User: serso
 * Date: 1/20/13
 * Time: 12:21 PM
 */
public final class FunctionBuilderAdapter implements MathEntityBuilder<Function> {

	@Nonnull
	private final AFunction.Builder nestedBuilder;

	public FunctionBuilderAdapter(@Nonnull AFunction.Builder nestedBuilder) {
		this.nestedBuilder = nestedBuilder;
	}

	@Nonnull
	@Override
	public MathEntityBuilder<Function> setName(@Nonnull String name) {
		nestedBuilder.setName(name);
		return this;
	}

	@Nonnull
	@Override
	public MathEntityBuilder<Function> setDescription(@Nullable String description) {
		nestedBuilder.setDescription(description);
		return this;
	}

	@Nonnull
	@Override
	public MathEntityBuilder<Function> setValue(@Nullable String value) {
		nestedBuilder.setValue(value);
		return this;
	}

	@Nonnull
	@Override
	public Function create() throws CustomFunctionCalculationException, AFunction.Builder.CreationException {
		final AFunction function = nestedBuilder.create();
		return new CustomFunction.Builder(function).create();
	}
}
