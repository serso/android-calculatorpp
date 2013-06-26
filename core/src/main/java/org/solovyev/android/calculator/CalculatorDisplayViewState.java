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

import jscl.math.Generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.solovyev.android.calculator.jscl.JsclOperation;

import java.io.Serializable;

/**
 * User: serso
 * Date: 9/20/12
 * Time: 9:50 PM
 */
public interface CalculatorDisplayViewState extends Serializable {

	@Nonnull
	String getText();

	int getSelection();

	@Nullable
	Generic getResult();

	boolean isValid();

	@Nullable
	String getErrorMessage();

	@Nonnull
	JsclOperation getOperation();

	@Nullable
	String getStringResult();
}
