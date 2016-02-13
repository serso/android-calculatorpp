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
import java.util.List;

public class Main {
    private static final Persister persister = new Persister();

    public static void main(String... args) throws Exception {
        final String inFileName = "app/src/main/res/values/text_converter.xml";
        final File inFile = new File(inFileName);

        final File outDir = new File("build/translations/res");
        delete(outDir);
        outDir.mkdirs();

        final Resources resources = persister.read(Resources.class, inFile);
        final List<String> languages = new ArrayList<>();
        languages.add("ar");
        languages.add("cs");
        languages.add("es");
        languages.add("de");
        languages.add("fi");
        languages.add("fr");
        languages.add("it");
        languages.add("nl");
        languages.add("pl");
        languages.add("pt");
        languages.add("ru");
        languages.add("tr");
        languages.add("vi");
        languages.add("ja");
        languages.add("ja");
        languages.add("zh");

        final CloseableHttpClient client = HttpClients.createDefault();
        try {
            for (String language : languages) {
                final Resources translations = new Resources();
                for (ResourceString string : resources.strings) {
                    final String translation = translate(client, string.value, language);
                    if (!TextUtils.isEmpty(translation)) {
                        translations.strings.add(new ResourceString(string.name, translation));
                    }
                }
                saveTranslations(translations, language, outDir, inFile.getName());
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
        final File dir = new File(outDir, "values-" + androidLanguage(language));
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

    private static String androidLanguage(String language) {
        switch (language) {
            case "pt":
                return "pt-rpt";
            case "zh":
                return "zh-rcn";
            default:
                return language;
        }
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
