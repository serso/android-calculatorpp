package org.solovyev.android.translations;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Android {

    private static class TranslationLink {
        public final String inName;
        public final String outName;

        private TranslationLink(String inName, String outName) {
            this.inName = inName;
            this.outName = outName;
        }
    }

    public static void main(String... args) throws Exception {
        final File inDir =
                new File("/home/serso/projects/java/aosp/platform/packages-apps-settings/res");
        final File outDir = new File("build/translations/res");
        Utils.delete(outDir);
        outDir.mkdirs();

        final List<TranslationLink> translationLinks = new ArrayList<>();
        translationLinks.add(new TranslationLink("haptic_feedback_enable_title", "cpp_prefs_vibrate_on_keypress"));
        translationLinks.add(new TranslationLink("accelerometer_title", "cpp_prefs_auto_rotate_screen"));
        translationLinks.add(new TranslationLink("phone_language", "cpp_prefs_language"));
        translationLinks.add(new TranslationLink("night_mode_title", "cpp_prefs_theme"));
        translationLinks.add(new TranslationLink("keep_screen_on", "cpp_prefs_keep_screen_on"));

        List<String> languageLocales = new ArrayList<>(Utils.languageLocales);
        languageLocales.add("");
        for (String languageLocale : languageLocales) {
            final File inLanguageDir = new File(inDir, Utils.valuesFolderName(languageLocale));
            final File inFile = new File(inLanguageDir, "strings.xml");
            final Resources resources = Utils.persister.read(Resources.class, inFile);
            Resources translations = new Resources();
            for (TranslationLink translationLink : translationLinks) {
                String translation = translate(resources, translationLink);
                if(!TextUtils.isBlank(translation)) {
                    translations.strings.add(new ResourceString(translationLink.outName, translation));
                }
            }
            Utils.saveTranslations(translations, languageLocale, outDir, "text_imported.xml");
        }
    }

    private static String translate(Resources resources, TranslationLink translationLink) {
        for (ResourceString string: resources.strings) {
            if (string.name.equals(translationLink.inName)) {
                if(TextUtils.isBlank(string.value)) {
                    return null;
                }
                if (string.value.length() >= 2 && string.value.startsWith("\"") && string.value
                        .endsWith("\"")) {
                    return string.value.substring(1, string.value.length() - 1);
                }
                return string.value;
            }
        }
        return null;
    }
}
