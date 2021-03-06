package com.ringosham.translationmod.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ringosham.translationmod.client.types.Language;
import com.ringosham.translationmod.client.types.RequestResult;
import com.ringosham.translationmod.client.types.google.TranslateError;
import com.ringosham.translationmod.client.types.google.TranslateResponse;
import com.ringosham.translationmod.common.ConfigManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GooglePaidClient {
    //This is the Google cloud translation API service
    //Rather over complicating things by logging in via OAuth/gcloud, we will just use the API key option to access the API
    //Logging in via gcloud requires the gcloud library. It's too big and unnecessary for a mod this small.
    //While OAuth is definitely more secure, since users are likely to create their own "project" in Google cloud console,
    //it's easier to just use an API key and it's more portable and sharable.
    private static final String baseUrl = "https://translation.googleapis.com/language/translate/v2";
    private static boolean disable = false;

    public static void setDisable() {
        disable = true;
    }

    public static boolean getDisable() {
        return disable;
    }

    public RequestResult translateAuto(String message, Language to) {
        return translate(message, LangManager.getInstance().getAutoLang(), to);
    }

    public RequestResult translate(String message, Language from, Language to) {
        Map<String, String> queryParam = new HashMap<>();
        String encodedMessage = null;
        //Percent encode message
        try {
            encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        //The query supports multiline using an array, however we only have one line to text to translate.
        queryParam.put("q", encodedMessage);
        //Query parameters
        queryParam.put("target", to.getGoogleCode());
        //Interpret text as... text. Apparently it defaults to HTML. What
        queryParam.put("format", "text");
        queryParam.put("key", ConfigManager.config.userKey.get());
        //Skip the source language for auto detection
        if (from != LangManager.getInstance().getAutoLang()) {
            queryParam.put("source", from.getGoogleCode());
        }
        RESTClient.Response response = RESTClient.INSTANCE.POST(baseUrl, queryParam);
        String responseString = response.getEntity();
        Gson gson = new Gson();
        if (response.getResponseCode() == 200) {
            TranslateResponse transResponse = gson.fromJson(responseString, TranslateResponse.class);
            //Just get the first element. Since this is a single element query there should not be more than 1 entry in the array
            String translatedText = transResponse.getData().getTranslations()[0].getTranslatedText();
            Language sourceLang = LangManager.getInstance().findLanguageFromGoogle(transResponse.getData().getTranslations()[0].getDetectedSourceLanguage());
            if (sourceLang == null)
                sourceLang = from;
            return new RequestResult(200, translatedText, sourceLang, to);
        } else {
            //Internal errors
            if (response.getResponseCode() < 100) {
                return new RequestResult(response.getResponseCode(), response.getEntity(), null, null);
            }
            try {
                TranslateError error = gson.fromJson(responseString, TranslateError.class);
                return new RequestResult(error.getError().getCode(), error.getError().getMessage(), null, null);
            } catch (JsonSyntaxException e) {
                //Unknown response
                return new RequestResult(2, "Unknown response: " + responseString, null, null);
            }
        }
    }
}
