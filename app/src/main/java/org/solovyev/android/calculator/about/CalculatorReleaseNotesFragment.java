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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.release.ReleaseNotes;

/**
 * User: serso
 * Date: 12/25/11
 * Time: 12:00 AM
 */
public class CalculatorReleaseNotesFragment extends CalculatorFragment {

    public CalculatorReleaseNotesFragment() {
        super(CalculatorFragmentType.release_notes);
    }

    @Override
    public void onViewCreated(View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final TextView releaseNotes = (TextView) root.findViewById(R.id.releaseNotesTextView);
        releaseNotes.setMovementMethod(LinkMovementMethod.getInstance());

        releaseNotes.setText(Html.fromHtml(ReleaseNotes.getReleaseNotes(this.getActivity())));
    }
}
