package org.solovyev.android.calculator.external;

import android.content.Context;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorDisplayViewState;
import org.solovyev.android.calculator.CalculatorEditorViewState;

/**
 * User: serso
 * Date: 11/20/12
 * Time: 10:42 PM
 */
public interface ExternalCalculatorStateUpdater {

	void updateState(@NotNull Context context,
					 @NotNull CalculatorEditorViewState editorState,
					 @NotNull CalculatorDisplayViewState displayState);
}
