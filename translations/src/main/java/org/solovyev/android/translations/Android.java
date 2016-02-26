package org.solovyev.android.translations;

import org.apache.commons.cli.*;
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
        final Options options = new Options();
        options.addOption(Option.builder("as").longOpt("aosp-settings").hasArg().desc("Local location of aosp/platform/packages/apps/settings").required().build());

        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine = parser.parse(options, args);
        final File aospSettings = new File(commandLine.getOptionValue("as"));
        if (!aospSettings.exists() || !aospSettings.isDirectory()) {
            throw new IllegalArgumentException(aospSettings + " doesn't exist or not a directory");
        }

        final File inDir =
                new File(aospSettings, "res");
        final File outDir = new File("build/translations/res");
        Utils.delete(outDir);
        outDir.mkdirs();

        final List<TranslationLink> translationLinks = new ArrayList<>();
        translationLinks.add(new TranslationLink("haptic_feedback_enable_title", "cpp_prefs_vibrate_on_keypress"));
        translationLinks.add(new TranslationLink("accelerometer_title", "cpp_prefs_auto_rotate_screen"));
        translationLinks.add(new TranslationLink("phone_language", "cpp_language"));
        translationLinks.add(new TranslationLink("night_mode_title", "cpp_theme"));
        translationLinks.add(new TranslationLink("night_mode_no", "cpp_theme_light"));
        translationLinks.add(new TranslationLink("night_mode_yes", "cpp_theme_dark"));
        translationLinks.add(new TranslationLink("keep_screen_on", "cpp_prefs_keep_screen_on"));
        translationLinks.add(new TranslationLink("draw_overlay", "cpp_permission_overlay"));
        translationLinks.add(new TranslationLink("yes", "cpp_yes"));
        translationLinks.add(new TranslationLink("no", "cpp_no"));
        translationLinks.add(new TranslationLink("create", "cpp_create"));
        translationLinks.add(new TranslationLink("dlg_close", "cpp_close"));
        translationLinks.add(new TranslationLink("dlg_switch", "cpp_switch"));

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
