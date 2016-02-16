package org.solovyev.android.translations;

import org.apache.http.util.TextUtils;
import org.simpleframework.xml.core.Persister;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    static final List<String> languageLocales = new ArrayList<>();
    static final Persister persister = new Persister();

    static void saveTranslations(Resources translations, String language, File outDir,
            String fileName) {
        final File dir = new File(outDir, valuesFolderName(language));
        dir.mkdirs();
        FileWriter out = null;
        try {
            out = new FileWriter(new File(dir, fileName));
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            persister.write(translations, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(out);
        }

    }

    static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean delete(File file) {
        if(!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        boolean deleted = true;
        final File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleted &= delete(child);
            }
        }
        return deleted && file.delete();
    }

    static String valuesFolderName(String languageLocale) {
        if(TextUtils.isEmpty(languageLocale)) {
            return "values";
        }
        return "values-" + languageLocale;
    }

    static {
        languageLocales.add("ar");
        languageLocales.add("cs");
        languageLocales.add("es");
        languageLocales.add("de");
        languageLocales.add("fi");
        languageLocales.add("fr");
        languageLocales.add("it");
        languageLocales.add("nl");
        languageLocales.add("pl");
        languageLocales.add("pt-rBR");
        languageLocales.add("pt-rPT");
        languageLocales.add("ru");
        languageLocales.add("tr");
        languageLocales.add("vi");
        languageLocales.add("ja");
        languageLocales.add("ja");
        languageLocales.add("zh-rCN");
        languageLocales.add("zh-rTW");
    }
}
