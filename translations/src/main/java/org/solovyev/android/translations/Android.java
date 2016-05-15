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
    private static final List<TranslationLink> platformLinks = new ArrayList<>();

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
        settingsLinks.add(new TranslationLink("location_mode_title", "cpp_mode"));
        settingsLinks.add(new TranslationLink("enable_text", "cpp_enable"));
        settingsLinks.add(new TranslationLink("storage_detail_other", "cpp_other"));
        settingsLinks.add(new TranslationLink("accessibility_toggle_high_text_contrast_preference_title", "cpp_high_contrast_text"));

        calendarLinks.add(new TranslationLink("edit_label", "cpp_edit"));
        calendarLinks.add(new TranslationLink("delete_label", "cpp_delete"));
        calendarLinks.add(new TranslationLink("save_label", "cpp_done"));
        calendarLinks.add(new TranslationLink("discard_label", "cpp_cancel"));
        calendarLinks.add(new TranslationLink("hint_description", "cpp_description"));
        calendarLinks.add(new TranslationLink("preferences_about_title", "cpp_about"));

        contactsLinks.add(new TranslationLink("copy_text", "cpp_copy_text"));
        contactsLinks.add(new TranslationLink("toast_text_copied", "cpp_text_copied"));
        contactsLinks.add(new TranslationLink("header_name_entry", "cpp_name"));
        contactsLinks.add(new TranslationLink("activity_title_settings", "cpp_settings"));

        calculatorLinks.add(new TranslationLink("error_nan", "cpp_nan"));
        calculatorLinks.add(new TranslationLink("error_syntax", "cpp_error"));

        platformLinks.add(new TranslationLink("copy", "cpp_copy"));
    }

    public static void main(String... args) throws Exception {
        final Options options = new Options();
        options.addOption(Option.builder("aosp").hasArg().desc("Local location of aosp project").required().build());
        options.addOption(Option.builder("project").hasArg().desc("Local location of Android project").build());
        options.addOption(Option.builder("resources").hasArg().desc("String identifiers to be copied").build());

        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine = parser.parse(options, args);

        final String aosp = commandLine.getOptionValue("aosp");
        final File aospSettings = makeInputDirectory(aosp + "/platform/packages/apps/settings");
        final File aospCalendar = makeInputDirectory(aosp + "/platform/packages/apps/calendar");
        final File aospContacts = makeInputDirectory(aosp + "/platform/packages/apps/contacts");
        final File aospCalculator = makeInputDirectory(aosp + "/platform/packages/apps/calculator");

        final String androidHome = System.getenv("ANDROID_HOME");
        if (TextUtils.isEmpty(androidHome)) {
            throw new MissingOptionException("ANDROID_HOME must be set");
        }
        final File androidPlatform = makeInputDirectory(androidHome + "/platforms/android-23/data");

        final File[] projects;
        if (commandLine.hasOption("project")) {
            final String[] projectPaths = commandLine.getOptionValues("project");
            projects = new File[projectPaths.length];
            for (int i = 0; i < projectPaths.length; i++) {
                projects[i] = makeInputDirectory(projectPaths[i]);
            }
        } else {
            projects = null;
        }
        final List<TranslationLink>[] projectsLinks;
        if (commandLine.hasOption("resources")) {
            final String[] projectResources = commandLine.getOptionValues("resources");
            projectsLinks = new List[projectResources.length];
            for (int j = 0; j < projectResources.length; j++) {
                final String resources = projectResources[j];
                projectsLinks[j] = new ArrayList<>();
                for (String resource : resources.split(",")) {
                    final int i = resource.indexOf("-");
                    if (i >= 0) {
                        projectsLinks[j].add(new TranslationLink(resource.substring(0, i), "cpp_" + resource.substring(i + 1, resource.length())));
                    } else {
                        projectsLinks[j].add(new TranslationLink(resource, "cpp_" + resource));
                    }
                }
            }
        } else {
            projectsLinks = null;
        }

        final File outDir = new File("build/translations/res");
        Utils.delete(outDir);
        outDir.mkdirs();

        translate(outDir, "aosp", new TranslationDef(aospSettings, settingsLinks), new TranslationDef(aospCalendar, calendarLinks), new TranslationDef(aospContacts, contactsLinks), new TranslationDef(aospCalculator, calculatorLinks), new TranslationDef(androidPlatform, platformLinks));
        if (projects != null && projects.length != 0) {
            if (projectsLinks == null || projectsLinks.length != projects.length) {
                throw new IllegalArgumentException("Projects=" + projects.length + ", resources=" + (projectsLinks == null ? 0 : projectsLinks.length));
            }
            for (int i = 0; i < projects.length; i++) {
                final File project = projects[i];
                final List<TranslationLink> projectLinks = projectsLinks[i];
                translate(outDir, "other" + (i == 0 ? "" : i), new TranslationDef(project, projectLinks));
            }
        }
    }

    private static void translate(File outDir, String outPostfix, TranslationDef... translationDefs) throws Exception {
        List<String> languageLocales = new ArrayList<>(Utils.languageLocales);
        languageLocales.add("");
        for (String languageLocale : languageLocales) {
            Resources translations = new Resources();
            for (TranslationDef def : translationDefs) {
                translate(readResources(def.project, languageLocale), translations, def.links);
            }
            Utils.saveTranslations(translations, languageLocale, outDir, "strings_imported_" + outPostfix + ".xml");
        }
    }

    private static Resources readResources(File from, String languageLocale) throws Exception {
        File inFile = makeStringsFile(from, languageLocale);
        if (!inFile.exists()) {
            final int i = languageLocale.indexOf("-r");
            if (i >= 0) {
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

    private static class TranslationDef {
        public final File project;
        public final List<TranslationLink> links;

        private TranslationDef(File project, List<TranslationLink> links) {
            this.project = project;
            this.links = links;
        }
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
