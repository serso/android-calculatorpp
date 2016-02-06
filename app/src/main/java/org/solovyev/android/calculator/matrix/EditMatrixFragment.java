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

import android.os.Bundle;
import android.view.View;

import org.solovyev.android.calculator.*;
import org.solovyev.android.view.IntegerRange;
import org.solovyev.android.view.Picker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.android.calculator.FragmentTab.matrix_edit;

public class EditMatrixFragment extends BaseFragment implements Picker.OnChangedListener<Integer> {
    private static final int MAX_COUNT = 10;
    private static final int MIN_COUNT = 2;
    private static final int DEFAULT_ROWS = 2;
    private static final int DEFAULT_COLS = 2;

    private static final String MATRIX = "matrix";

    public EditMatrixFragment() {
        setRetainInstance(true);
    }

    @Nonnull
    @Override
    protected FragmentUi createUi() {
        return createUi(matrix_edit);
    }

    @Override
    public void onViewCreated(View root, @Nullable Bundle in) {
        super.onViewCreated(root, in);

        final Picker<Integer> matrixRowsCountPicker = (Picker<Integer>) root.findViewById(R.id.matrix_rows_count_picker);
        initPicker(matrixRowsCountPicker);
        final Picker<Integer> matrixColsCountPicker = (Picker<Integer>) root.findViewById(R.id.matrix_cols_count_picker);
        initPicker(matrixColsCountPicker);

        MatrixUi matrix = null;
        if (in != null) {
            final Object matrixObject = in.getSerializable(MATRIX);
            if (matrixObject instanceof MatrixUi) {
                matrix = (MatrixUi) matrixObject;
            }
        }

        final MatrixView matrixView = getMatrixView(root);
        if (matrix == null) {
            matrixView.setMatrixDimensions(DEFAULT_ROWS, DEFAULT_COLS);
        } else {
            matrixView.setMatrix(matrix.getBakingArray());
        }
        matrixRowsCountPicker.setCurrent(matrixView.getRows());
        matrixColsCountPicker.setCurrent(matrixView.getCols());
    }

    @Override
    public void onSaveInstanceState(@Nonnull Bundle out) {
        super.onSaveInstanceState(out);

        out.putSerializable(MATRIX, new MatrixUi(getMatrixView(getView()).toMatrix()));
    }

    @Nonnull
    private MatrixView getMatrixView(@Nonnull View root) {
        return (MatrixView) root.findViewById(R.id.matrix_layout);
    }

    private void initPicker(@Nonnull Picker<Integer> picker) {
        picker.setRange(new IntegerRange(MIN_COUNT, MAX_COUNT, 1, 0, null));
        picker.setOnChangeListener(this);
    }

    @Override
    public void onChanged(@Nonnull Picker picker, @Nonnull Integer value) {
        switch (picker.getId()) {
            case R.id.matrix_rows_count_picker:
                onRowsCountChange(value);
                break;
            case R.id.matrix_cols_count_picker:
                onColsCountChange(value);
                break;
        }
    }

    private void onColsCountChange(@Nonnull Integer newCols) {
        getMatrixView(getView()).setMatrixCols(newCols);
    }

    private void onRowsCountChange(@Nonnull Integer newRows) {
        getMatrixView(getView()).setMatrixRows(newRows);
    }
}
