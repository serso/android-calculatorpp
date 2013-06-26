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

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import javax.annotation.Nonnull;

import org.solovyev.android.Android;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.CalculatorFragmentType;
import org.solovyev.android.calculator.R;
import org.solovyev.common.text.Strings;

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

		releaseNotes.setText(Html.fromHtml(getReleaseNotes(this.getActivity())));
	}

	@Nonnull
	public static String getReleaseNotes(@Nonnull Context context) {
		return getReleaseNotes(context, 0);
	}

	@Nonnull
	public static String getReleaseNotes(@Nonnull Context context, int minVersion) {
		final StringBuilder result = new StringBuilder();

		final String releaseNotesForTitle = context.getString(R.string.c_release_notes_for_title);
		final int version = Android.getAppVersionCode(context);

		final TextHelper textHelper = new TextHelper(context.getResources(), CalculatorApplication.class.getPackage().getName());

		boolean first = true;
		for (int i = version; i >= minVersion; i--) {
			String releaseNotesForVersion = textHelper.getText("c_release_notes_for_" + i);
			if (!Strings.isEmpty(releaseNotesForVersion)) {
				assert releaseNotesForVersion != null;
				if (!first) {
					result.append("<br/><br/>");
				} else {
					first = false;
				}
				releaseNotesForVersion = releaseNotesForVersion.replace("\n", "<br/>");
				result.append("<b>").append(releaseNotesForTitle).append(i).append("</b><br/><br/>");
				result.append(releaseNotesForVersion);
			}
		}

		return result.toString();
	}
}
