package org.solovyev.android.translations;

import org.apache.commons.cli.*;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Android {

    private static final List<TranslationLink> settingsLinks = new ArrayList<>();
    private static final List<TranslationLink> calendarLinks = new ArrayList<>();
    private static final List<TranslationLink> contactsLinks = new ArrayList<>();
    private static final List<TranslationLink> calculatorLinks = new ArrayList<>();

    static {
        settingsLinks.add(new TranslationLink("haptic_feedback_enable_title", "cpp_prefs_vibrate_on_keypress"));
        settingsLinks.add(new TranslationLink("accelerometer_title", "cpp_prefs_auto_rotate_screen"));
        settingsLinks.add(new TranslationLink("phone_language", "cpp_language"));
        settingsLinks.add(new TranslationLink("night_mode_title", "cpp_theme"));
        settingsLinks.add(new TranslationLink("night_mode_no", "cpp_theme_light"));
        settingsLinks.add(new TranslationLink("night_mode_yes", "cpp_theme_dark"));
        settingsLinks.add(new TranslationLink("keep_screen_on", "cpp_prefs_keep_screen_on"));
        settingsLinks.add(new TranslationLink("draw_overlay", "cpp_permission_overlay"));
        settingsLinks.add(new TranslationLink("yes", "cpp_yes"));
        settingsLinks.add(new TranslationLink("no", "cpp_no"));
        settingsLinks.add(new TranslationLink("create", "cpp_create"));
        settingsLinks.add(new TranslationLink("dlg_close", "cpp_close"));
        settingsLinks.add(new TranslationLink("dlg_switch", "cpp_switch"));
        settingsLinks.add(new TranslationLink("user_dict_settings_add_menu_title", "cpp_add"));

        calendarLinks.add(new TranslationLink("edit_label", "cpp_edit"));
        calendarLinks.add(new TranslationLink("delete_label", "cpp_delete"));
        calendarLinks.add(new TranslationLink("save_label", "cpp_done"));
        calendarLinks.add(new TranslationLink("discard_label", "cpp_cancel"));
        calendarLinks.add(new TranslationLink("hint_description", "cpp_description"));

        contactsLinks.add(new TranslationLink("copy_text", "cpp_copy_text"));
        contactsLinks.add(new TranslationLink("toast_text_copied", "cpp_text_copied"));
        contactsLinks.add(new TranslationLink("header_name_entry", "cpp_name"));

        calculatorLinks.add(new TranslationLink("error_nan", "cpp_nan"));
        calculatorLinks.add(new TranslationLink("error_syntax", "cpp_error"));
    }

    public static void main(String... args) throws Exception {
        final Options options = new Options();
        options.addOption(Option.builder("aosp").hasArg().desc("Local location of aosp project").required().build());

        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine = parser.parse(options, args);

        final String aosp = commandLine.getOptionValue("aosp");
        final File aospSettings = makeInputDirectory(aosp + "/platform/packages/apps/settings");
        final File aospCalendar = makeInputDirectory(aosp + "/platform/packages/apps/calendar");
        final File aospContacts = makeInputDirectory(aosp + "/platform/packages/apps/contacts");
        final File aospCalculator = makeInputDirectory(aosp + "/platform/packages/apps/calculator");

        final File outDir = new File("build/translations/res");
        Utils.delete(outDir);
        outDir.mkdirs();

        List<String> languageLocales = new ArrayList<>(Utils.languageLocales);
        languageLocales.add("");
        for (String languageLocale : languageLocales) {
            Resources translations = new Resources();
            translate(readResources(aospSettings, languageLocale), translations, settingsLinks);
            translate(readResources(aospCalendar, languageLocale), translations, calendarLinks);
            translate(readResources(aospContacts, languageLocale), translations, contactsLinks);
            translate(readResources(aospCalculator, languageLocale), translations, calculatorLinks);
            Utils.saveTranslations(translations, languageLocale, outDir, "text_imported.xml");
        }
    }

    private static Resources readResources(File from, String languageLocale) throws Exception {
        File inFile = makeStringsFile(from, languageLocale);
        if(!inFile.exists()) {
            final int i = languageLocale.indexOf("-r");
            if(i >= 0) {
                inFile = makeStringsFile(from, languageLocale.substring(0, i));
            }
        }
        return Utils.persister.read(Resources.class, inFile);
    }

    private static File makeStringsFile(File from, String languageLocale) {
        return new File(new File(from, Utils.valuesFolderName(languageLocale)), "strings.xml");
    }

    private static File makeInputDirectory(String dirName) {
        final File dir = new File(dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException(dir + " doesn't exist or not a directory");
        }

        return new File(dir, "res");
    }

    private static void translate(Resources from, Resources to, List<TranslationLink> links) {
        for (TranslationLink translationLink : links) {
            String translation = translate(from, translationLink);
            if (!TextUtils.isBlank(translation)) {
                to.strings.add(new ResourceString(translationLink.outName, translation));
            }
        }
    }

    private static String translate(Resources resources, TranslationLink translationLink) {
        for (ResourceString string : resources.strings) {
            if (string.name.equals(translationLink.inName)) {
                if (TextUtils.isBlank(string.value)) {
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

    private static class TranslationLink {
        public final String inName;
        public final String outName;

        private TranslationLink(String inName, String outName) {
            this.inName = inName;
            this.outName = outName;
        }
    }
}
