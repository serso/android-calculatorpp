package org.solovyev.android.translations;

import org.apache.commons.codec.Charsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Wiki {
    public static void main(String... args) throws Exception {
        final String inFileName = "app/src/main/res/values/strings_converter.xml";
        final File inFile = new File(inFileName);

        final File outDir = new File("build/translations/res");
        Utils.delete(outDir);
        outDir.mkdirs();

        final Resources resources = Utils.persister.read(Resources.class, inFile);

        final CloseableHttpClient client = HttpClients.createDefault();
        try {
            final Map<String, Resources> allTranslations = new HashMap<>();
            for (String languageLocale : Utils.languageLocales) {
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
                Utils.saveTranslations(translations, languageLocale, outDir, inFile.getName());
            }

        } finally {
            Utils.close(client);
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
            Utils.close(response);
        }
        return null;
    }

    private static String toLanguage(String languageLocale) {
        final int i = languageLocale.indexOf('-');
        if(i >= 0) {
            return languageLocale.substring(0, i);
        }
        return languageLocale;
    }
}
