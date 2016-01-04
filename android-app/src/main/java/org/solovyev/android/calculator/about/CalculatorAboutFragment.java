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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;

import static android.view.View.GONE;
import static org.solovyev.common.text.Strings.isEmpty;

/**
 * User: serso
 * Date: 12/24/11
 * Time: 11:55 PM
 */
public class CalculatorAboutFragment extends CalculatorFragment {

    public CalculatorAboutFragment() {
        super(CalculatorFragmentType.about);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        if (App.getTheme().isLight()) {
            final ImageView image = (ImageView) root.findViewById(R.id.about_image);
            image.setImageResource(R.drawable.logo_wizard_light);
        }

        final TextView aboutTextView = (TextView) root.findViewById(R.id.cpp_about_textview);
        aboutTextView.setMovementMethod(LinkMovementMethod.getInstance());

        final TextView translatorsTextTextView = (TextView) root.findViewById(R.id.cpp_about_translators_text);
        final TextView translatorsTextView = (TextView) root.findViewById(R.id.cpp_about_translators);
        if (isEmpty(translatorsTextView.getText())) {
            translatorsTextTextView.setVisibility(GONE);
            translatorsTextView.setVisibility(GONE);
        }
    }
}
