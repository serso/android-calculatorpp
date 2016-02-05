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

package org.solovyev.android.calculator.about;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.solovyev.android.calculator.BaseFragment;
import org.solovyev.android.calculator.FragmentUi;
import org.solovyev.android.calculator.R;

import javax.annotation.Nonnull;

import static android.view.View.GONE;
import static org.solovyev.android.calculator.CalculatorFragmentType.about;
import static org.solovyev.common.text.Strings.isEmpty;

public class AboutFragment extends BaseFragment {

    @Bind(R.id.about_image)
    ImageView imageView;
    @Bind(R.id.about_text)
    TextView textView;
    @Bind(R.id.about_translators_label)
    TextView translatorsLabel;
    @Bind(R.id.about_translators)
    TextView translatorsView;

    @Nonnull
    @Override
    protected FragmentUi createUi() {
        return createUi(about);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        if (ui.getTheme().light) {
            imageView.setImageResource(R.drawable.logo_wizard_light);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        if (isEmpty(translatorsView.getText())) {
            translatorsLabel.setVisibility(GONE);
            translatorsView.setVisibility(GONE);
        }
        return view;
    }
}
