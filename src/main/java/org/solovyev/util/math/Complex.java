/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 */

package org.solovyev.util.math;

import org.jetbrains.annotations.Nullable;

/**
* User: serso
* Date: 9/17/11
* Time: 11:35 PM
*/
public class Complex {

	@Nullable
	private Double real;

	@Nullable
	private Double imaginary;

	@Nullable
	public Double getReal() {
		return real;
	}

	public void setReal(@Nullable Double real) {
		this.real = real;
	}

	@Nullable
	public Double getImaginary() {
		return imaginary;
	}

	public void setImaginary(@Nullable Double imaginary) {
		this.imaginary = imaginary;
	}
}
