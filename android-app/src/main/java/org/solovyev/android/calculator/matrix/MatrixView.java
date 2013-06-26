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

package org.solovyev.android.calculator.matrix;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Solovyev_S
 * Date: 12.10.12
 * Time: 15:41
 */
public class MatrixView extends TableLayout {

	/*
	**********************************************************************
	*
	*                           CONSTANTS
	*
	**********************************************************************
	*/

	private static final CharSequence DEFAULT_CELL_TEXT = "0";
	private static final int NUMBER_INDEX = -1;


	/*
	**********************************************************************
	*
	*                           FIELDS
	*
	**********************************************************************
	*/

	private int rows = 0;
	private int cols = 0;

	@Nullable
	private CharSequence defaultCellText = DEFAULT_CELL_TEXT;

	private boolean initialized = false;

	/*
	**********************************************************************
	*
	*                           CONSTRUCTORS
	*
	**********************************************************************
	*/

	public MatrixView(Context context) {
		super(context);
	}

	public MatrixView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/*
	**********************************************************************
	*
	*                           METHODS
	*
	**********************************************************************
	*/

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public void setMatrixCols(int newCols) {
		setMatrixDimensions(rows, newCols);
	}

	public void setMatrixRows(int newRows) {
		setMatrixDimensions(newRows, cols);
	}

	public void setMatrixDimensions(int newRows, int newCols) {
		if (newRows <= 1) {
			throw new IllegalArgumentException("Number of rows must be more than 1: " + newRows);
		}

		if (newCols <= 1) {
			throw new IllegalArgumentException("Number of columns must be more than 1: " + newCols);
		}

		boolean rowsChanged = this.rows != newRows;
		boolean colsChanged = this.cols != newCols;

		if (rowsChanged || colsChanged) {
			if (!initialized) {
				addRow(NUMBER_INDEX, 0);
				initialized = true;
			}

			if (this.cols > newCols) {
				removeCols(newCols);
			} else if (this.cols < newCols) {
				addCols(newCols);
			}

			this.cols = newCols;

			if (this.rows > newRows) {
				removeRows(newRows);
			} else if (this.rows < newRows) {
				addRows(newRows);
			}

			this.rows = newRows;
		}
	}

	public void setMatrix(@Nonnull Object[][] matrix) {
		final int rows = matrix.length;
		final int cols = matrix[0].length;

		setMatrixDimensions(rows, cols);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				setCell(row, col, matrix[row][col]);
			}
		}
	}

	@Nonnull
	public String[][] toMatrix() {
		final String[][] result = new String[rows][cols];

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				final TextView cellTextView = (TextView) getCell(this, row, col);
				if (cellTextView != null) {
					result[row][col] = cellTextView.getText().toString();
				}
			}
		}

		return result;
	}

	private void setCell(int row, int col, @Nullable Object o) {
		final TextView cellTextView = (TextView) getCell(this, row, col);
		if (cellTextView != null) {
			if (o == null) {
				cellTextView.setText(null);
			} else {
				cellTextView.setText(String.valueOf(o));
			}
		}
	}

	/*
	**********************************************************************
	*
	*                           PRIVATE METHODS
	*
	**********************************************************************
	*/

	private void addRows(int newRows) {
		for (int row = this.rows; row < newRows; row++) {
			addRow(row, cols);
		}
	}

	private void removeRows(int newRows) {
		for (int row = this.rows - 1; row >= newRows; row--) {
			removeRow(row);
		}
	}

	private void addCols(int newCols) {
		for (int row = NUMBER_INDEX; row < rows; row++) {
			final ViewGroup rowView = getRow(row);
			if (rowView != null) {
				for (int col = this.cols; col < newCols; col++) {
					rowView.addView(createCellView(row, col));
				}
			}
		}
	}

	private void removeCols(int newCols) {
		for (int row = NUMBER_INDEX; row < rows; row++) {
			final ViewGroup rowView = getRow(row);
			if (rowView != null) {
				for (int col = this.cols - 1; col >= newCols; col--) {
					final View cellView = getCell(rowView, row, col);
					if (cellView != null) {
						rowView.removeView(cellView);
					}
				}
			}
		}
	}

	private void addRow(int row, int newCols) {
		this.addView(createRowView(row, newCols));
	}

	private void removeRow(int row) {
		final View rowView = getRow(row);
		if (rowView != null) {
			this.removeView(rowView);
		}
	}

	@Nullable
	private TableRow getRow(int row) {
		return (TableRow) this.findViewWithTag(getRowTag(row));
	}

	@Nullable
	private View getCell(@Nonnull View view, int row, int col) {
		return view.findViewWithTag(getCellTag(row, col));
	}

	@Nonnull
	private String getRowTag(int row) {
		if (row != NUMBER_INDEX) {
			return "row_" + row;
		} else {
			return "row_index";
		}
	}

	@Nonnull
	private View createRowView(int row, int cols) {
		final ViewGroup rowView = new TableRow(this.getContext());

		rowView.setTag(getRowTag(row));

		if (row != NUMBER_INDEX) {
			rowView.addView(createCellView(row, NUMBER_INDEX));
		} else {
			// empty cell
			rowView.addView(new View(this.getContext()));
		}

		for (int col = 0; col < cols; col++) {
			rowView.addView(createCellView(row, col));
		}
		return rowView;
	}

	@Nonnull
	private View createCellView(int row, int col) {
		final TextView result;

		if (row != NUMBER_INDEX && col != NUMBER_INDEX) {
			result = new EditText(this.getContext());
			result.setText(defaultCellText);
		} else {
			result = new TextView(this.getContext());
			if (row == NUMBER_INDEX) {
				result.setText(String.valueOf(col + 1));
			} else {
				result.setText(String.valueOf(row + 1));
			}
		}

		result.setTag(getCellTag(row, col));

		return result;

	}

	@Nonnull
	private String getCellTag(int row, int col) {
		if (row != NUMBER_INDEX) {
			return "cell_" + row + "_" + col;
		} else {
			return "cell_index_" + col;
		}
	}

}
