package org.solovyev.android.calculator.matrix;

import android.os.Bundle;
import android.view.View;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.IntegerRange;
import org.solovyev.android.view.Picker;

import java.io.Serializable;

/**
 * User: Solovyev_S
 * Date: 12.10.12
 * Time: 10:41
 */
public class CalculatorMatrixEditFragment extends CalculatorFragment implements Picker.OnChangedListener<Integer> {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	private static final int MAX_COUNT = 10;
	private static final int MIN_COUNT = 2;
	private static final int DEFAULT_ROWS = 2;
	private static final int DEFAULT_COLS = 2;

	private static final String MATRIX = "matrix";


	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	public CalculatorMatrixEditFragment() {
		super(CalculatorFragmentType.matrix_edit);

		setRetainInstance(true);
	}

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/


	@Override
	public void onViewCreated(View root, @Nullable Bundle in) {
		super.onViewCreated(root, in);

		final Picker<Integer> matrixRowsCountPicker = (Picker<Integer>) root.findViewById(R.id.matrix_rows_count_picker);
		initPicker(matrixRowsCountPicker);
		final Picker<Integer> matrixColsCountPicker = (Picker<Integer>) root.findViewById(R.id.matrix_cols_count_picker);
		initPicker(matrixColsCountPicker);

		Matrix matrix = null;
		if (in != null) {
			final Object matrixObject = in.getSerializable(MATRIX);
			if (matrixObject instanceof Matrix) {
				matrix = (Matrix) matrixObject;
			}
		}

		final MatrixView matrixView = getMatrixView(root);
		if (matrix == null) {
			matrixView.setMatrixDimensions(DEFAULT_ROWS, DEFAULT_COLS);
		} else {
			matrixView.setMatrix(matrix.bakingArray);
		}
		matrixRowsCountPicker.setCurrent(matrixView.getRows());
		matrixColsCountPicker.setCurrent(matrixView.getCols());
	}

	@Override
	public void onSaveInstanceState(@NotNull Bundle out) {
		super.onSaveInstanceState(out);

		out.putSerializable(MATRIX, new Matrix(getMatrixView(getView()).toMatrix()));
	}

	@NotNull
	private MatrixView getMatrixView(@NotNull View root) {
		return (MatrixView) root.findViewById(R.id.matrix_layout);
	}

	private void initPicker(@NotNull Picker<Integer> picker) {
		picker.setRange(new IntegerRange(MIN_COUNT, MAX_COUNT, 1, 0, null));
		picker.setOnChangeListener(this);
	}

	@Override
	public void onChanged(@NotNull Picker picker, @NotNull Integer value) {
		switch (picker.getId()) {
			case R.id.matrix_rows_count_picker:
				onRowsCountChange(value);
				break;
			case R.id.matrix_cols_count_picker:
				onColsCountChange(value);
				break;
		}
	}

	private void onColsCountChange(@NotNull Integer newCols) {
		getMatrixView(getView()).setMatrixCols(newCols);
	}

	private void onRowsCountChange(@NotNull Integer newRows) {
		getMatrixView(getView()).setMatrixRows(newRows);
	}

	public static class Matrix implements Serializable {

		@NotNull
		private String[][] bakingArray;

		public Matrix() {
		}

		public Matrix(@NotNull String[][] bakingArray) {
			this.bakingArray = bakingArray;
		}
	}
}
