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
	private String real, imag;

	@Nullable
	public String getReal() {
		return real;
	}

	public void setReal(@Nullable String real) {
		this.real = real;
	}

	@Nullable
	public String getImag() {
		return imag;
	}

	public void setImag(@Nullable String imag) {
		this.imag = imag;
	}
}
