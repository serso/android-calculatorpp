/*
 * Copyright (c) 2009-2011. Created by serso aka se.solovyev.
 * For more information, please, contact se.solovyev@gmail.com
 * or visit http://se.solovyev.org
 */

package org.solovyev.android.calculator.about;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.solovyev.android.calculator.CalculatorActivity;
import org.solovyev.android.calculator.R;
import org.solovyev.android.view.prefs.AndroidUtils;
import org.solovyev.android.view.prefs.ResourceCache;
import org.solovyev.common.utils.StringUtils;

/**
 * User: serso
 * Date: 12/25/11
 * Time: 12:00 AM
 */
public class CalculatorReleaseNotesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.release_notes);

		final TextView releaseNotes = (TextView) findViewById(R.id.releaseNotesTextView);
		releaseNotes.setMovementMethod(LinkMovementMethod.getInstance());

		releaseNotes.setText(Html.fromHtml(getReleaseNotes(this)));
			

	}

	@NotNull
	public static String getReleaseNotes(@NotNull Context context) {
		return getReleaseNotes(context, 0);
	}

	@NotNull
	public static String getReleaseNotes(@NotNull Context context, int minVersion) {
		final StringBuilder result = new StringBuilder();

		final String releaseNotesForTitle = context.getString(R.string.c_release_notes_for_title);
		final int version = AndroidUtils.getAppVersionCode(context, CalculatorActivity.class.getPackage().getName());

		boolean first = true;
		for ( int i = version; i >= minVersion; i-- ) {
			String releaseNotesForVersion = ResourceCache.instance.getCaption("c_release_notes_for_" + i);
			if (!StringUtils.isEmpty(releaseNotesForVersion)){
				assert releaseNotesForVersion != null;
				if ( !first ) {
					result.append("<br/><br/>");
				} else {
					first = false;
				}
				releaseNotesForVersion = releaseNotesForVersion.replace("\n", "<br/>");
				result.append("<b>").append(releaseNotesForTitle).append(i).append("</b><br/>");
				result.append(releaseNotesForVersion);
			}
		}

		return result.toString();
	}
}
