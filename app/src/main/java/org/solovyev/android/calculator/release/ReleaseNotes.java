package org.solovyev.android.calculator.release;

import android.content.Context;
import android.util.SparseArray;
import org.solovyev.android.calculator.App;
import org.solovyev.android.calculator.R;
import org.solovyev.common.text.Strings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class ReleaseNotes {

    private static final SparseArray<ReleaseNote> map = new SparseArray<>();
    static {
        map.put(141, ReleaseNote.make("2.1.2", R.string.cpp_release_notes_141));
        map.put(143, ReleaseNote.make("2.1.4", R.string.cpp_release_notes_143));
        map.put(148, ReleaseNote.make("2.2.1", R.string.cpp_release_notes_148));
        map.put(150, ReleaseNote.make("2.2.2", R.string.cpp_release_notes_150));
    }

    @Nonnull
    public static String getReleaseNotes(@Nonnull Context context) {
        return getReleaseNotesString(context, 0);
    }

    @Nonnull
    public static String getReleaseNoteVersion(int version) {
        final ReleaseNote releaseNote = map.get(version);
        return releaseNote == null ? String.valueOf(version) : releaseNote.versionName;
    }

    @Nonnull
    public static String getReleaseNoteDescription(@Nonnull Context context, int version) {
        final ReleaseNote releaseNote = map.get(version);
        return releaseNote == null ? "" : getDescription(context, releaseNote.description);
    }

    @Nonnull
    public static String getReleaseNotesString(@Nonnull Context context, int minVersion) {
        final StringBuilder result = new StringBuilder();

        final String releaseNotesForTitle = context.getString(R.string.c_release_notes_for_title);
        final int currentVersionCode = App.getAppVersionCode(context);

        boolean first = true;
        for (int versionCode = currentVersionCode; versionCode >= minVersion; versionCode--) {
            final ReleaseNote releaseNote = map.get(versionCode);
            if (releaseNote == null) {
                continue;
            }
            if (!first) {
                result.append("<br/><br/>");
            } else {
                first = false;
            }
            final String descriptionHtml = getDescription(context, releaseNote.description);
            result.append("<b>").append(releaseNotesForTitle).append(releaseNote.versionName).append("</b><br/><br/>");
            result.append(descriptionHtml);
        }

        return result.toString();
    }

    @Nonnull
    private static String getDescription(@Nonnull Context context, int description) {
        return context.getResources().getString(description).replace("\n", "<br/>");
    }

    @Nonnull
    public static List<Integer> getReleaseNotesVersions(@Nonnull Context context, int minVersion) {
        final List<Integer> releaseNotes = new ArrayList<>();

        final int currentVersionCode = App.getAppVersionCode(context);

        for (int versionCode = currentVersionCode; versionCode >= minVersion; versionCode--) {
            if (versionCode == ChooseThemeReleaseNoteStep.VERSION_CODE) {
                releaseNotes.add(ChooseThemeReleaseNoteStep.VERSION_CODE);
            }
            final ReleaseNote releaseNote = map.get(versionCode);
            if (releaseNote == null) {
                continue;
            }
            final String description = context.getString(releaseNote.description);
            if (!Strings.isEmpty(description)) {
                releaseNotes.add(versionCode);
            }
        }

        return releaseNotes;
    }

    public static boolean hasReleaseNotes(@Nonnull Context context, int minVersion) {
        final int currentVersionCode = App.getAppVersionCode(context);

        for (int versionCode = currentVersionCode; versionCode >= minVersion; versionCode--) {
            if (versionCode == ChooseThemeReleaseNoteStep.VERSION_CODE) {
                return true;
            }
            final ReleaseNote releaseNote = map.get(versionCode);
            if (releaseNote == null) {
                continue;
            }
            if (!Strings.isEmpty(context.getString(releaseNote.description))) {
                return true;
            }
        }

        return false;
    }
}
