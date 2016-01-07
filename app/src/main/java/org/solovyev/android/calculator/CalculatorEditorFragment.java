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

package org.solovyev.android.calculator;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.solovyev.android.menu.ActivityMenu;
import org.solovyev.android.menu.AndroidMenuHelper;
import org.solovyev.android.menu.ListActivityMenu;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 25.09.12
 * Time: 10:49
 */
public class CalculatorEditorFragment extends Fragment {

    @Nonnull
    private FragmentUi fragmentUi;

    @Nonnull
    private ActivityMenu<Menu, MenuItem> menu = ListActivityMenu.fromEnum(CalculatorMenu.class, AndroidMenuHelper.getInstance());

    @Nonnull
    private EditorView editorView;

    public CalculatorEditorFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentUi.onViewCreated(this, view);

        editorView = (EditorView) view.findViewById(R.id.calculator_editor);
        Locator.getInstance().getEditor().setView(editorView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        final Preferences.Gui.Layout layout = Preferences.Gui.getLayout(prefs);
        if (!layout.isOptimized()) {
            fragmentUi = CalculatorApplication.getInstance().createFragmentHelper(R.layout.cpp_app_editor_mobile, R.string.editor);
        } else {
            fragmentUi = CalculatorApplication.getInstance().createFragmentHelper(R.layout.cpp_app_editor, R.string.editor);
        }

        fragmentUi.onCreate(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return fragmentUi.onCreateView(this, inflater, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.fragmentUi.onResume(this);
    }

    @Override
    public void onPause() {
        this.fragmentUi.onPause(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Locator.getInstance().getEditor().clearView(editorView);
        fragmentUi.onDestroyView(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        fragmentUi.onDestroy(this);
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
