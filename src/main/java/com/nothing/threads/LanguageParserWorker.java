package com.nothing.threads;

import com.nothing.Language;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class LanguageParserWorker implements Callable<Boolean> {

    private final String url;
    private final Language language;

    public LanguageParserWorker(String url, Language language) {
        this.url = url;
        this.language = language;
    }

    @Override
    public Boolean call() {
        String plainHtml = getPlainHtml(url);
        String language = detectLanguageFromPlainHtml(plainHtml);
        boolean result = this.language.name().equalsIgnoreCase(language);
        System.out.println(Thread.currentThread().getName() + " : " + url + " : Detected language (expected: "+this.language.name().toLowerCase()+") -> " + language + " : Pass?? -> " + result);
        return result;
    }

    private String detectLanguageFromPlainHtml(String plainHtml) {
        LanguageDetector defaultLanguageDetector = LanguageDetector.getDefaultLanguageDetector();
        try {
            defaultLanguageDetector.loadModels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(plainHtml);
        String text = doc.text();
        LanguageResult detect = defaultLanguageDetector.detect(text);
        return detect.getLanguage();
    }


    private String getPlainHtml(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
           e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; ru; rv:1.9.0.11) Gecko/2009060215 Firefox/3.0.11 (.NET CLR 3.5.30729)");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s).append("\n");
            }
            inputStream.close();
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
