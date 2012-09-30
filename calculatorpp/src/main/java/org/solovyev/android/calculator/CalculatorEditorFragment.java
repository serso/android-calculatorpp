package org.solovyev.android.calculator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.ListActivityMenu;
import org.solovyev.android.sherlock.menu.SherlockMenuHelper;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 10:49
 */
public class CalculatorEditorFragment extends SherlockFragment {

    @NotNull
    private ActivityMenu<Menu, MenuItem> menu = ListActivityMenu.fromList(CalculatorMenu.class, SherlockMenuHelper.getInstance());

    private CalculatorFragmentHelper fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.calc_editor, R.string.editor);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentHelper.onCreate(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentHelper.onCreateView(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentHelper.onViewCreated(this, view);

        ((AndroidCalculator) CalculatorLocatorImpl.getInstance().getCalculator()).setEditor(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        fragmentHelper.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.fragmentHelper.onResume(this);
    }

    @Override
    public void onPause() {
        this.fragmentHelper.onPause(this);

        super.onPause();
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
