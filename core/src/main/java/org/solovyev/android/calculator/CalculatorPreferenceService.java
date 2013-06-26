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

import jscl.AngleUnit;
import jscl.NumeralBase;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 11/17/12
 * Time: 7:45 PM
 */
public interface CalculatorPreferenceService {

	void setPreferredAngleUnits();

	void setAngleUnits(@Nonnull AngleUnit angleUnit);

	void setPreferredNumeralBase();

	void setNumeralBase(@Nonnull NumeralBase numeralBase);

	void checkPreferredPreferences(boolean force);
}
