package org.solovyev.android.calculator.matrix;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
* User: serso
* Date: 7/11/13
* Time: 4:54 PM
*/
class MatrixUi implements Serializable {

	@Nonnull
	private String[][] bakingArray;

	public MatrixUi() {
	}

	public MatrixUi(@Nonnull String[][] bakingArray) {
		this.bakingArray = bakingArray;
	}

	@Nonnull
	String[][] getBakingArray() {
		return bakingArray;
	}
}
