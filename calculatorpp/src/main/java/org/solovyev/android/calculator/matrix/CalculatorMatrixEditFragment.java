package org.solovyev.android.calculator.matrix;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.about.CalculatorFragmentType;
import org.solovyev.android.view.IntegerRange;
import org.solovyev.android.view.Picker;

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

    /*
    **********************************************************************
    *
    *                           CONSTRUCTORS
    *
    **********************************************************************
    */

    public CalculatorMatrixEditFragment() {
        super(CalculatorFragmentType.matrix_edit);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final Picker<Integer> matrixRowsCountPicker = (Picker<Integer>) root.findViewById(R.id.matrix_rows_count_picker);
        initPicker(matrixRowsCountPicker);
        final Picker<Integer> matrixColsCountPicker = (Picker<Integer>) root.findViewById(R.id.matrix_cols_count_picker);
        initPicker(matrixColsCountPicker);

        getMatrixTable(root);
    }

    @NotNull
    private TableLayout getMatrixTable(@NotNull View root) {
        return (TableLayout) root.findViewById(R.id.matrix_layout);
    }

    private void initPicker(@NotNull Picker<Integer> picker) {
        picker.setRange(new IntegerRange(MIN_COUNT, MAX_COUNT, 1, 2, null));
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

    private void onColsCountChange(@NotNull Integer cols) {
        final TableLayout matrixTable = getMatrixTable(getView());
    }

    private void onRowsCountChange(@NotNull Integer rows) {
    }
}
