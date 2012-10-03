package org.solovyev.android.calculator;

import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.about.CalculatorFragmentType;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.ListActivityMenu;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 10:49
 */
public class CalculatorEditorFragment extends CalculatorFragment {

    @NotNull
    private ActivityMenu<Menu, MenuItem> menu = ListActivityMenu.fromList(CalculatorMenu.class, SherlockMenuHelper.getInstance());

    public CalculatorEditorFragment() {
        super(CalculatorFragmentType.editor);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AndroidCalculator) CalculatorLocatorImpl.getInstance().getCalculator()).setEditor(getActivity());
    }

    /*
    **********************************************************************
    *
    *                           MENU
    *
    **********************************************************************
    */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu.onCreateOptionsMenu(this.getActivity(), menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        this.menu.onPrepareOptionsMenu(this.getActivity(), menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.menu.onOptionsItemSelected(this.getActivity(), item);
    }
}
