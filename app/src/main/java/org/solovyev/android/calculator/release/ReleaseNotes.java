package org.solovyev.android.calculator.release;

import android.content.Context;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.CalculatorApplication;
import org.solovyev.android.calculator.R;
import org.solovyev.android.calculator.about.TextHelper;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ReleaseNotes {

    @Nonnull
    public static String getReleaseNotes(@Nonnull Context context) {
        return getReleaseNotesString(context, 0);
    }

    @Nonnull
    public static String getReleaseNotesString(@Nonnull Context context, int minVersion) {
        final StringBuilder result = new StringBuilder();

        final String releaseNotesForTitle = context.getString(R.string.c_release_notes_for_title);
        final int currentVersionCode = App.getAppVersionCode(context);

        final TextHelper textHelper = new TextHelper(context.getResources(), CalculatorApplication.class.getPackage().getName());

        boolean first = true;
        for (int versionCode = currentVersionCode; versionCode >= minVersion; versionCode--) {
            final String versionName = getVersionName(textHelper, versionCode);
            String releaseNotesForVersion = textHelper.getText(makeReleaseNotesResourceId(versionCode));
            if (!Strings.isEmpty(releaseNotesForVersion)) {
                if (!first) {
                    result.append("<br/><br/>");
                } else {
                    first = false;
                }
                releaseNotesForVersion = releaseNotesForVersion.replace("\n", "<br/>");
                result.append("<b>").append(releaseNotesForTitle).append(versionName).append("</b><br/><br/>");
                result.append(releaseNotesForVersion);
            }
        }

        return result.toString();
    }

    @Nonnull
    public static List<Integer> getReleaseNotesVersions(@Nonnull Context context, int minVersion) {
        final List<Integer> releaseNotes = new ArrayList<>();

        final int currentVersionCode = App.getAppVersionCode(context);
        final TextHelper textHelper = new TextHelper(context.getResources(), CalculatorApplication.class.getPackage().getName());

        for (int versionCode = currentVersionCode; versionCode >= minVersion; versionCode--) {
            if (versionCode == ChooseThemeReleaseNoteStep.VERSION_CODE) {
                releaseNotes.add(ChooseThemeReleaseNoteStep.VERSION_CODE);
            }
            final String releaseNotesForVersion = textHelper.getText(makeReleaseNotesResourceId(versionCode));
            if (!Strings.isEmpty(releaseNotesForVersion)) {
                releaseNotes.add(versionCode);
            }
        }

        return releaseNotes;
    }

    public static boolean hasReleaseNotes(@Nonnull Context context, int minVersion) {
        final int currentVersionCode = App.getAppVersionCode(context);
        final TextHelper textHelper = new TextHelper(context.getResources(), CalculatorApplication.class.getPackage().getName());

        for (int versionCode = currentVersionCode; versionCode >= minVersion; versionCode--) {
            if (versionCode == ChooseThemeReleaseNoteStep.VERSION_CODE) {
                return true;
            }
            String releaseNotesForVersion = textHelper.getText(makeReleaseNotesResourceId(versionCode));
            if (!Strings.isEmpty(releaseNotesForVersion)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    public static String getVersionName(@Nonnull TextHelper textHelper, int versionCode) {
        final String versionName = textHelper.getText(makeVersionResourceId(versionCode));
        if (versionName != null) {
            return versionName;
        } else {
            return String.valueOf(versionCode);
        }
    }

    public static String makeReleaseNotesResourceId(int versionCode) {
        return "c_release_notes_for_" + versionCode;
    }

    private static String makeVersionResourceId(int versionCode) {
        return "c_release_notes_for_" + versionCode + "_version";
    }
}
