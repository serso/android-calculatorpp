/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.about;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.AndroidUtils;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.CalculatorFragment;
import org.solovyev.android.calculator.R;
import org.solovyev.common.text.StringUtils;

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

    @NotNull
	public static String getReleaseNotes(@NotNull Context context) {
		return getReleaseNotes(context, 0);
	}

	@NotNull
	public static String getReleaseNotes(@NotNull Context context, int minVersion) {
		final StringBuilder result = new StringBuilder();

		final String releaseNotesForTitle = context.getString(R.string.c_release_notes_for_title);
		final int version = AndroidUtils.getAppVersionCode(context, CalculatorApplication.class.getPackage().getName());

        final TextHelper textHelper = new TextHelper(context.getResources(), CalculatorApplication.class.getPackage().getName());

		boolean first = true;
		for ( int i = version; i >= minVersion; i-- ) {
			String releaseNotesForVersion = textHelper.getText("c_release_notes_for_" + i);
			if (!StringUtils.isEmpty(releaseNotesForVersion)){
				assert releaseNotesForVersion != null;
				if ( !first ) {
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
