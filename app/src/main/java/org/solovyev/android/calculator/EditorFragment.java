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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;

import javax.inject.Inject;

import static org.solovyev.android.calculator.App.cast;

public class EditorFragment extends Fragment {

    private FragmentUi ui;

    private EditorView editorView;

    @Inject
    Editor editor;
    @Inject
    SharedPreferences preferences;
    @Inject
    ActivityLauncher launcher;

    public EditorFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cast(getActivity().getApplication()).getComponent().inject(this);

        final Preferences.Gui.Layout layout = Preferences.Gui.getLayout(preferences);
        if (!layout.optimized) {
            ui = new FragmentUi(R.layout.cpp_app_editor_mobile, R.string.editor);
        } else {
            ui = new FragmentUi(R.layout.cpp_app_editor, R.string.editor);
        }

        ui.onCreate(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ui.onViewCreated(this, view);

        editorView = (EditorView) view.findViewById(R.id.calculator_editor);
        editor.setView(editorView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return ui.onCreateView(this, inflater, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.ui.onResume(this);
    }

    @Override
    public void onPause() {
        this.ui.onPause(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        editor.clearView(editorView);
        ui.onDestroyView(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        ui.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                launcher.showSettings();
                return true;
            case R.id.menu_history:
                launcher.showHistory();
                return true;
            case R.id.menu_plotter:
                Locator.getInstance().getPlotter().plot();
                return true;
            case R.id.menu_conversion_tool:
                new NumeralBaseConverterDialog(null).show(getActivity());
                return true;
            case R.id.menu_about:
                launcher.showAbout();
                return true;
        }
        return false;
    }
}
