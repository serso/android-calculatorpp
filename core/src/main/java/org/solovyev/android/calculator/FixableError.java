package org.solovyev.android.calculator;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * User: serso
 * Date: 12/2/12
 * Time: 10:21 PM
 */
public interface FixableError extends Serializable {

	@Nullable
	CharSequence getFixCaption();

    void fix();
}
