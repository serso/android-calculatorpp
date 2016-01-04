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

package org.solovyev.acraanalyzer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.solovyev.common.collections.Collections.getFirstCollectionElement;
import static org.solovyev.common.text.Strings.isEmpty;

/**
 * User: serso
 * Date: 6/12/13
 * Time: 10:08 PM
 */
public final class AcraAnalyzer {

    private static final String NEW_LINE = System.getProperty("line.separator");

    public static void main(String[] args) throws ParseException, FileNotFoundException {
        final Options options = new Options();
        options.addOption("path", true, "Path to the ACRA reports");
        options.addOption("file", true, "File name of MBOX file");
        options.addOption("version", true, "Version of the app");
        options.addOption("out", true, "Output file");

        final CommandLineParser parser = new GnuParser();
        final CommandLine cmd = parser.parse(options, args);
        final String path = cmd.getOptionValue("path");
        final String file = cmd.getOptionValue("file");
        final String version = cmd.getOptionValue("version");
        final String out = cmd.getOptionValue("out");

        if (isEmpty(path) && isEmpty(file)) {
            throw new IllegalArgumentException("Either path or filename should be specified");
        } else if (!isEmpty(path) && !isEmpty(file)) {
            throw new IllegalArgumentException("Specify either path or filename");
        } else {
            final Map<String, List<AcraReport>> reports = new HashMap<String, List<AcraReport>>();

            if (!isEmpty(path)) {
                scanFiles(path, reports, version);
            } else {
                scanMbox(file, reports, version);
            }

            PrintStream outIs;
            if (!isEmpty(out)) {
                final File outFile = new File(out);
                outIs = new PrintStream(outFile);
                System.out.println("Output will be written to " + outFile.getPath());
            } else {
                outIs = System.out;
            }

            final List<List<AcraReport>> sortedReports = new ArrayList<List<AcraReport>>(reports.size());
            for (Map.Entry<String, List<AcraReport>> entry : reports.entrySet()) {
                sortedReports.add(entry.getValue());
            }

            Collections.sort(sortedReports, new Comparator<Collection<AcraReport>>() {
                @Override
                public int compare(Collection<AcraReport> lhs, Collection<AcraReport> rhs) {
                    if (lhs.size() == rhs.size()) {
                        return 0;
                    } else if (lhs.size() < rhs.size()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });

            if (!sortedReports.isEmpty()) {
                for (Collection<AcraReport> sortedReport : sortedReports) {
                    final AcraReport report = getFirstCollectionElement(sortedReport);
                    outIs.println("Count: " + sortedReport.size());
                    outIs.println("App version: " + report.appVersion);
                    outIs.println("Android versions: " + getAndroidVersions(sortedReport));
                    outIs.println("Phones: " + getPhones(sortedReport));
                    outIs.println("Stack trace: " + report.stackTrace);
                }
            } else {
                outIs.println("No ACRA reports found");
            }
        }
    }

    @Nonnull
    private static String getPhones(@Nonnull Collection<AcraReport> reports) {
        final Set<String> versions = new HashSet<String>();
        for (AcraReport report : reports) {
            versions.add(report.brand + "/" + report.phoneModel);
        }
        return versions.toString();
    }

    @Nonnull
    private static String getAndroidVersions(@Nonnull Collection<AcraReport> reports) {
        final Set<String> versions = new HashSet<String>();
        for (AcraReport report : reports) {
            versions.add(report.androidVersion);
        }
        return versions.toString();
    }

    private static void scanMbox(@Nonnull String filename, @Nonnull Map<String, List<AcraReport>> reports, @Nullable String version) {
        final File file = new File(filename);
        if (!file.isDirectory()) {
            Scanner scanner = null;

            try {
                scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    final AcraReport report = readReport(scanner);
                    putReport(reports, report, version);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }
    }

    private static void scanFiles(@Nonnull String path, @Nonnull Map<String, List<AcraReport>> reports, @Nullable String version) {
        final File directory = new File(path);
        if (directory.isDirectory()) {
            scanFiles(directory, reports, version);
        }
    }

    private static void scanFiles(@Nonnull File directory, @Nonnull Map<String, List<AcraReport>> reports, @Nullable String version) {
        final File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanFiles(file, reports, version);
                } else {
                    analyzeFile(file, reports, version);
                }
            }
        }
    }

    private static void analyzeFile(File file, Map<String, List<AcraReport>> reports, @Nullable String version) {
        final AcraReport report = readReport(file);
        putReport(reports, report, version);
    }

    private static void putReport(@Nonnull Map<String, List<AcraReport>> reports, @Nonnull AcraReport report, @Nullable String version) {
        if (!isEmpty(report.stackTrace) && (version == null || version.equals(report.appVersion))) {
            List<AcraReport> acraReports = reports.get(report.stackTrace);
            if (acraReports == null) {
                acraReports = new ArrayList<AcraReport>();
                reports.put(report.stackTrace, acraReports);
            }
            acraReports.add(report);
        }
    }

    private static AcraReport readReport(@Nonnull File file) {
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
            return readReport(scanner);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return new AcraReport();
    }

    @Nonnull
    private static AcraReport readReport(@Nonnull Scanner scanner) {
        final AcraReport result = new AcraReport();

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.startsWith("STACK_TRACE")) {
                result.stackTrace = readStackTrace(line.substring("STACK_TRACE=".length()), scanner);
                break;
            } else if (line.startsWith("ANDROID_VERSION")) {
                result.androidVersion = line.substring("ANDROID_VERSION=".length());
            } else if (line.startsWith("APP_VERSION_NAME")) {
                result.appVersion = line.substring("APP_VERSION_NAME=".length());
            } else if (line.startsWith("BRAND")) {
                result.brand = line.substring("BRAND=".length());
            } else if (line.startsWith("USER_COMMENT")) {
                result.userComment = line.substring("USER_COMMENT=".length());
            } else if (line.startsWith("PHONE_MODEL")) {
                result.phoneModel = line.substring("PHONE_MODEL=".length());
            }
        }

        return result;
    }

    private static String readStackTrace(String firstLine, Scanner scanner) {
        final StringBuilder sb = new StringBuilder();
        sb.append(firstLine.trim()).append(newLine());

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine().trim();
            if (isEmpty(line)) {
                break;
            } else {
                sb.append(line).append(newLine());
            }
        }

        return sb.toString();
    }

    public static String newLine() {
        return NEW_LINE;
    }

    private static final class AcraReport {
        private String userComment;
        private String phoneModel;
        private String brand;
        private String appVersion;
        private String androidVersion;
        private String stackTrace = "";

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AcraReport that = (AcraReport) o;

            if (!stackTrace.equals(that.stackTrace)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return stackTrace.hashCode();
        }
    }
}
