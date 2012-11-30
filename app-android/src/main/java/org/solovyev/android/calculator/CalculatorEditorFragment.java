package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    private CalculatorFragmentHelper fragmentHelper;

    @NotNull
    private ActivityMenu<Menu, MenuItem> menu = ListActivityMenu.fromList(CalculatorMenu.class, SherlockMenuHelper.getInstance());

    public CalculatorEditorFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentHelper.onViewCreated(this, view);

        ((AndroidCalculator) Locator.getInstance().getCalculator()).setEditor(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        final CalculatorPreferences.Gui.Layout layout = CalculatorPreferences.Gui.getLayout(prefs);
        if (layout == CalculatorPreferences.Gui.Layout.main_calculator_mobile) {
            fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.calc_editor_mobile, R.string.editor);
        } else {
            fragmentHelper = CalculatorApplication.getInstance().createFragmentHelper(R.layout.calc_editor, R.string.editor);
        }

        fragmentHelper.onCreate(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentHelper.onCreateView(this, inflater, container);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        fragmentHelper.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
