package org.solovyev.android.translations;

import org.apache.commons.codec.Charsets;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Microsoft {

    private static final Pattern TRANSLATION_REGEX = Pattern.compile("<TranslatedText>(.+?)</TranslatedText>");

    private static final String xmlVersions = "<ter:Versions>\n" +
            "<ter:Version>\n" +
            "<ter:Name>${version}</ter:Name>\n" +
            "</ter:Version>\n" +
            "</ter:Versions>\n";
    private static final String xmlPre = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ter=\"http://api.terminology.microsoft.com/terminology\">\n" +
            "<soapenv:Header/>\n" +
            "<soapenv:Body>\n" +
            "<ter:GetTranslations>\n" +
            "<ter:text>${text}</ter:text>\n" +
            "<ter:from>en-us</ter:from>\n" +
            "<ter:to>${to}</ter:to>\n" +
            "<ter:searchOperator>Contains</ter:searchOperator>\n" +
            "<ter:sources>\n" +
            "<ter:TranslationSource>UiStrings</ter:TranslationSource>\n" +
            "</ter:sources>\n" +
            "<ter:unique>false</ter:unique>\n" +
            "<ter:maxTranslations>1</ter:maxTranslations>\n" +
            "<ter:includeDefinitions>true</ter:includeDefinitions>\n" +
            "<ter:products>\n" +
            "<ter:Product>\n" +
            "<ter:Name>${product}</ter:Name>\n";
    private static final String xmlPost = "</ter:Product>\n" +
            "</ter:products>\n" +
            "</ter:GetTranslations>\n" +
            "</soapenv:Body>\n" +
            "</soapenv:Envelope>";
    private static final String xml = xmlPre +
            xmlVersions +
            xmlPost;
    private static final String xmlNoVersion = xmlPre + xmlPost;

    public static void main(String... args) throws Exception {
        final String inFileName = "app/src/main/res/values/text_microsoft.xml";
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
                        if (TextUtils.isEmpty(string.comment)) {
                            System.err.println("No product/version for " + string.value);
                            continue;
                        }
                        final String[] comments = string.comment.split("-");
                        final String translation = translate(client, string.value, language, comments[0], comments.length > 1 ? comments[1] : "");
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

    private static String translate(CloseableHttpClient client, String word, String language, String product, String version)
            throws UnsupportedEncodingException {
        final HttpPost request = new HttpPost("http://api.terminology.microsoft.com/Terminology.svc");
        request.addHeader("Content-Type", "text/xml; charset=utf-8");
        request.addHeader("SOAPAction", "\"http://api.terminology.microsoft.com/terminology/Terminology/GetTranslations\"");
        final String xml;
        if (version.length() == 0) {
            xml = Microsoft.xmlNoVersion;
        } else {
            xml = Microsoft.xml.replace("${version}", version);
        }
        final String body = xml.replace("${text}", word).replace("${to}", language).replace("${product}", product);
        request.setEntity(new StringEntity(body, Charsets.UTF_8));
        CloseableHttpResponse response = null;
        try {
            response = client.execute(request);
            final String result = EntityUtils.toString(response.getEntity());
            if (TextUtils.isEmpty(result)) {
                System.err.println("No translation for " + word + " in " + language);
                return null;
            }
            final Matcher matcher = TRANSLATION_REGEX.matcher(result);
            if (!matcher.find()) {
                System.err.println("No translation for " + word + " in " + language);
                return null;
            }
           return matcher.group(1);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        } finally {
            Utils.close(response);
        }
        return null;
    }

    private static String toLanguage(String languageLocale) {
        switch (languageLocale) {
            case "en":
                return "en-us";
            case "cs":
                return "cs-cz";
            case "ar":
                return "ar-sa";
            case "vi":
                return "vi-vn";
            case "ja":
                return "ja-jp";
            case "uk":
                return "uk-ua";
            case "pt-rBR":
                return "pt-br";
            case "pt-rPT":
                return "pt-pt";
            case "zh-rTW":
                return "zh-tw";
            case "zh-rCN":
                return "zh-cn";
            default:
                final int i = languageLocale.indexOf('-');
                final String language = i >= 0 ? languageLocale.substring(0, i) : languageLocale;
                return language + "-" + language;
        }
    }
}
