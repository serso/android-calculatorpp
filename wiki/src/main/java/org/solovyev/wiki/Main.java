package org.solovyev.wiki;

import org.apache.commons.codec.Charsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Persister persister = new Persister();

    public static void main(String... args) throws Exception {
        final String inFileName = "app/src/main/res/values/text_converter.xml";
        final File inFile = new File(inFileName);

        final File outDir = new File("build/translations/res");
        delete(outDir);
        outDir.mkdirs();

        final Resources resources = persister.read(Resources.class, inFile);
        final List<String> languageLocales = new ArrayList<>();
        languageLocales.add("ar");
        languageLocales.add("cs");
        languageLocales.add("es");
        languageLocales.add("de");
        languageLocales.add("fi");
        languageLocales.add("fr");
        languageLocales.add("it");
        languageLocales.add("nl");
        languageLocales.add("pl");
        languageLocales.add("pt-rbr");
        languageLocales.add("pt-rpt");
        languageLocales.add("ru");
        languageLocales.add("tr");
        languageLocales.add("vi");
        languageLocales.add("ja");
        languageLocales.add("ja");
        languageLocales.add("zh-rcn");
        languageLocales.add("zh-rtw");

        final CloseableHttpClient client = HttpClients.createDefault();
        try {
            final Map<String, Resources> allTranslations = new HashMap<>();
            for (String languageLocale : languageLocales) {
                final String language = toLanguage(languageLocale);
                Resources translations = allTranslations.get(language);
                if (translations == null) {
                    translations = new Resources();
                    allTranslations.put(language, translations);
                    for (ResourceString string : resources.strings) {
                        final String translation = translate(client, string.value, language);
                        if (!TextUtils.isEmpty(translation)) {
                            translations.strings.add(new ResourceString(string.name, translation));
                        }
                    }
                }
                saveTranslations(translations, languageLocale, outDir, inFile.getName());
            }

        } finally {
            close(client);
        }
    }

    private static boolean delete(File file) {
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

    private static String translate(CloseableHttpClient client, String word, String language)
            throws UnsupportedEncodingException {
        final String uri =
                "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=langlinks&titles="
                        + URLEncoder.encode(word, Charsets.UTF_8.toString()) + "&lllang="
                        + language;
        final HttpGet request = new HttpGet(
                uri);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            final String result = EntityUtils.toString(response.getEntity());
            if (TextUtils.isEmpty(result)) {
                System.out.println("No translation for " + word);
                return null;
            }
            final JSONObject json = new JSONObject(result);
            final JSONObject jsonQuery = json.getJSONObject("query");
            final JSONObject jsonPages = jsonQuery.getJSONObject("pages");
            for (String key : jsonPages.keySet()) {
                final JSONObject jsonPage = jsonPages.getJSONObject(key);
                final JSONArray jsonLangLinks = jsonPage.getJSONArray("langlinks");
                if (jsonLangLinks.length() > 0) {
                    final JSONObject jsonLangLink = jsonLangLinks.getJSONObject(0);
                    final String translation = jsonLangLink.getString("*");
                    if (TextUtils.isBlank(translation)) {
                        return null;
                    }
                    final int i = translation.lastIndexOf(" (");
                    if(i >= 0) {
                        return translation.substring(0, i);
                    }
                    return translation;
                }
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            System.err.println("Uri=" + uri);
        } finally {
            close(response);
        }
        return null;
    }

    private static void saveTranslations(Resources translations, String language, File outDir, String fileName) {
        final File dir = new File(outDir, "values-" + language);
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

    private static String toLanguage(String languageLocale) {
        final int i = languageLocale.indexOf('-');
        if(i >= 0) {
            return languageLocale.substring(0, i);
        }
        return languageLocale;
    }

    private static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Root
    public static class Resources {
        @ElementList(inline = true)
        public List<ResourceString> strings = new ArrayList<>();

        public Resources() {
        }
    }

    @SuppressWarnings("unused")
    @Root(name = "string")
    public static class ResourceString {
        @Attribute
        public String name;
        @Text
        public String value;

        public ResourceString() {
        }

        private ResourceString(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
