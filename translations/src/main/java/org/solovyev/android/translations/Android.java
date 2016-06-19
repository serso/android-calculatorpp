package org.solovyev.android.translations;

import org.apache.commons.cli.*;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Android {

    public static void main(String... args) throws Exception {
        final Options options = new Options();
        options.addOption(Option.builder("prefix").hasArg().desc("Local location of Android project").required(false).build());
        options.addOption(Option.builder("project").hasArg().desc("Local location of Android project").build());
        options.addOption(Option.builder("resources").hasArg().desc("String identifiers to be copied").build());
        options.addOption(Option.builder("output").hasArg().desc("Output folder").required().build());
        options.addOption(Option.builder("languages").hasArg().desc("Comma-separated list of languages for translation").required().build());

        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine = parser.parse(options, args);

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
        final String prefix = makePrefix(commandLine.getOptionValue("prefix"));
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
                        projectsLinks[j].add(new TranslationLink(resource.substring(0, i), prefix + resource.substring(i + 1, resource.length())));
                    } else {
                        projectsLinks[j].add(new TranslationLink(resource, prefix + resource));
                    }
                }
            }
        } else {
            projectsLinks = null;
        }

        final List<String> languageLocales = new ArrayList<>();
        languageLocales.addAll(Arrays.asList(commandLine.getOptionValue("languages").split(",")));
        languageLocales.add("");

        final File outDir = new File(commandLine.getOptionValue("output"));
        Utils.delete(outDir);
        final File outResDir = new File(outDir, "res");
        outResDir.mkdirs();

        if (projects != null && projects.length != 0) {
            if (projectsLinks == null || projectsLinks.length != projects.length) {
                throw new IllegalArgumentException("Projects=" + projects.length + ", resources=" + (projectsLinks == null ? 0 : projectsLinks.length));
            }
            for (int i = 0; i < projects.length; i++) {
                final File project = projects[i];
                final List<TranslationLink> projectLinks = projectsLinks[i];
                translate(outResDir, languageLocales, "other" + (i == 0 ? "" : i), new TranslationDef(project, projectLinks));
            }
        }
    }

    private static String makePrefix(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return "";
        }
        return prefix + "_";
    }

    private static void translate(File outDir, List<String> languageLocales, String outPostfix, TranslationDef... translationDefs) throws Exception {
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
