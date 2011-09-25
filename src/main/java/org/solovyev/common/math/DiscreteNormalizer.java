package org.solovyev.common.math;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: serso
 * Date: 9/20/11
 * Time: 5:42 PM
 */
public class DiscreteNormalizer implements Normalizer {

	@NotNull
	private final Normalizer normalizer;

	private final double min;

	private final double step;

	public DiscreteNormalizer(double min, double max, int steps) {
		this(min, max, steps, null);
	}

	public DiscreteNormalizer(double min, double max, int steps, @Nullable Normalizer normalizer) {
		assert min <= max;
		assert steps > 1;

		if (normalizer != null) {
			this.normalizer = normalizer;
		} else {
			this.normalizer = new LinearNormalizer(min, max);
		}

		this.step = this.normalizer.normalize((max - min) / (steps - 1));
		this.min = this.normalizer.normalize(min);
	}

	public DiscreteNormalizer(double min, double max, double step) {
		assert min <= max;
		assert step > 0;

		this.normalizer = new LinearNormalizer(min, max);

		this.step = normalizer.normalize(step);
		this.min = normalizer.normalize(min);
	}

	@Override
	public double normalize(double value) {
		double normalizedValue = normalizer.normalize(value);

		double result = min;
		while (true) {
			if ( result + step > normalizedValue ) {
				break;
			} else {
				result += step;
			}
		}

		return result;
	}

	@Override
	public double denormalize(double value) {
		return normalizer.denormalize(value);
	}
}
