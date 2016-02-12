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

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String... args) throws UnsupportedEncodingException {
        final List<String> words = new ArrayList<>();
        words.add("Time");
        words.add("Amount of substance");
        words.add("Electric current");
        words.add("Length");
        words.add("Mass");
        words.add("Temperature");
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
                final Map<String, String> translations = new HashMap<>();
                for (String word : words) {
                    final String translation = translate(client, word, language);
                    if (!TextUtils.isEmpty(translation)) {
                        translations.put(word, translation);
                    }
                }
                writeTranslations(translations, language);
            }

        } finally {
            close(client);
        }
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
                    return jsonLangLink.getString("*");
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

    private static void writeTranslations(Map<String, String> translations, String language) {
        File dir = new File("out");
        dir.mkdirs();
        FileWriter out = null;
        try {
            out = new FileWriter(new File(dir, language + ".xml"));
            out.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            out.write("<resources>\n");
            for (Map.Entry<String, String> entry : translations.entrySet()) {
                out.write("<string name=\"" + entry.getKey() + "\">" + entry.getValue()
                        + "</string>\n");
            }
            out.write("</resources>\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(out);
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
}
