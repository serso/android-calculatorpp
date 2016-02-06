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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.view.NumeralBaseConverterDialog;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class EditorFragment extends BaseFragment {

    @Inject
    Editor editor;
    @Inject
    SharedPreferences preferences;
    @Inject
    ActivityLauncher launcher;
    @Bind(R.id.calculator_editor)
    EditorView editorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void inject(@Nonnull AppComponent component) {
        super.inject(component);
        component.inject(this);
    }

    @Nonnull
    @Override
    protected FragmentUi createUi() {
        final Preferences.Gui.Layout layout = Preferences.Gui.getLayout(preferences);
        if (!layout.optimized) {
            return new FragmentUi(R.layout.cpp_app_editor_mobile, R.string.editor);
        } else {
            return new FragmentUi(R.layout.cpp_app_editor, R.string.editor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        editor.setView(editorView);
        return view;
    }

    @Override
    public void onDestroyView() {
        editor.clearView(editorView);
        ui.onDestroyView();
        super.onDestroyView();
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
