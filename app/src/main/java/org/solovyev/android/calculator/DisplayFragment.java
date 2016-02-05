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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class DisplayFragment extends BaseFragment {

    @Bind(R.id.calculator_display)
    DisplayView displayView;
    @Inject
    SharedPreferences preferences;
    @Inject
    Display display;

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
            return new FragmentUi(R.layout.cpp_app_display_mobile, R.string.result);
        } else {
            return new FragmentUi(R.layout.cpp_app_display, R.string.result);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        display.setView(displayView);
        return view;
    }

    @Override
    public void onDestroyView() {
        display.clearView(displayView);
        super.onDestroyView();
    }
}
