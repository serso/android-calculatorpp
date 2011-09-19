/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.common.math;

/**
* User: serso
* Date: 9/19/11
* Time: 9:31 PM
*/
public class LinearNormalizer {

	private final double min;
	private final double max;

	public LinearNormalizer(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public double normalize(double value){
		if ((max - min) != 0d) {
			return (value - min) / (max - min);
		} else {
			return 1d;
		}
	}

	public double denormalize(double value){
		return min + value * (max - min);
	}

}
