package org.solovyev.acraanalyzer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.cli.*;
import org.solovyev.common.text.Strings;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.solovyev.common.collections.Collections.getFirstCollectionElement;

/**
 * User: serso
 * Date: 6/12/13
 * Time: 10:08 PM
 */
public final class AcraAnalyzer {

	private static final String NEW_LINE = System.getProperty("line.separator");

	public static void main(String[] args) throws ParseException {
		final Options options = new Options();
		options.addOption("path", true, "Path to the ACRA reports");

		final CommandLineParser parser = new GnuParser();
		final CommandLine cmd = parser.parse(options, args);
		final String path = cmd.getOptionValue("path");
		if (Strings.isEmpty(path)) {
			throw new IllegalArgumentException("Path should be specified");
		} else {
			final Multimap<String, AcraReport> reports = ArrayListMultimap.create(100, 20);
			scanFiles(path, reports);
			final List<Collection<AcraReport>> sortedReports = new ArrayList<Collection<AcraReport>>(reports.size());
			for (String stackTrace : reports.keys()) {
				sortedReports.add(reports.get(stackTrace));
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

			for (Collection<AcraReport> sortedReport : sortedReports) {
				final AcraReport report = getFirstCollectionElement(sortedReport);
				System.out.println("Count: " + sortedReport.size());
				System.out.println("App version: " + report.appVersion);
				System.out.println("Stack trace: " + report.stackTrace);
			}
		}
	}

	private static void scanFiles(String path, Multimap<String, AcraReport> reports) {
		final File directory = new File(path);
		if (directory.isDirectory()) {
			scanFiles(directory, reports);
		}
	}

	private static void scanFiles(File directory, Multimap<String, AcraReport> reports) {
		final File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					scanFiles(file, reports);
				} else {
					analyzeFile(file, reports);
				}
			}
		}
	}

	private static void analyzeFile(File file, Multimap<String, AcraReport> reports) {
		final AcraReport report = readReport(file);
		if (!Strings.isEmpty(report.stackTrace)) {
			reports.put(report.stackTrace, report);
		}
	}

	private static AcraReport readReport(File file) {
		final AcraReport result = new AcraReport();

		Scanner scanner = null;

		try {
			scanner = new Scanner(file);
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}

		return result;
	}

	private static String readStackTrace(String firstLine, Scanner scanner) {
		final StringBuilder sb = new StringBuilder();
		sb.append(firstLine.trim()).append(newLine());

		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine().trim();
			if (line.startsWith("at")) {
				sb.append(line).append(newLine());
			} else {
				break;
			}
		}

		return sb.toString();
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

	public static String newLine() {
		return NEW_LINE;
	}
}
