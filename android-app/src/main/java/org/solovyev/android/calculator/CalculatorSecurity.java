/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 1/4/12
 * Time: 1:23 AM
 */
public final class CalculatorSecurity {

	private CalculatorSecurity() {
	}

	@Nonnull
	public static String getPK() {
		final StringBuilder result = new StringBuilder();

		result.append("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A");
		result.append("MIIBCgKCAQEAquP2a7dEhTaJEQeXtSyreH5dCmTDOd");
		result.append("dElCfg0ijOeB8JTxBiJTXLWnLA0kMaT/sRXswUaYI61YCQOoik82");
		result.append("qrFH7W4+OFtiLb8WGX+YPEpQQ/IBZu9qm3xzS9Nolu79EBff0/CLa1FuT9RtjO");
		result.append("iTW8Q0VP9meQdJEkfqJEyVCgHain+MGoQaRXI45EzkYmkz8TBx6X6aJF5NBAXnAWeyD0wPX1");
		result.append("uedHH7+LgLcjnPVw82YjyJSzYnaaD2GX0Y7PGoFe6J5K4yJGGX5mih45pe2HWcG5lAkQhu1uX2hCcCBdF3");
		result.append("W7paRq9mJvCsbn+BNTh9gq8QKui0ltmiWpa5U+/9L+FQIDAQAB");

		return result.toString();
	}
}
