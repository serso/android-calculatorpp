package org.solovyev.common.math;

import org.jetbrains.annotations.NotNull;

/**
 * User: serso
 * Date: 9/20/11
 * Time: 5:42 PM
 */
public class DiscreteNormalizer implements Normalizer {

	@NotNull
	private final LinearNormalizer linearNormalizer;

	private final double min;

	private final double step;

	public DiscreteNormalizer(double min, double max, int steps) {
		assert min <= max;
		assert steps > 1;

		this.linearNormalizer = new LinearNormalizer(min, max);

		this.step = linearNormalizer.normalize((max - min) / (steps - 1));
		this.min = linearNormalizer.normalize(min);
	}

	public DiscreteNormalizer(double min, double max, double step) {
		assert min <= max;
		assert step > 0;

		this.linearNormalizer = new LinearNormalizer(min, max);

		this.step = linearNormalizer.normalize(step);
		this.min = linearNormalizer.normalize(min);
	}

	@Override
	public double normalize(double value) {
		double normalizedValue = linearNormalizer.normalize(value);

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
		return linearNormalizer.denormalize(value);
	}
}
