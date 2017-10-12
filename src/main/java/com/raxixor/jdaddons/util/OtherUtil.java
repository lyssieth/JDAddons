package com.raxixor.jdaddons.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.istack.internal.NotNull;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class OtherUtil {
    
    private static final String SHORTENER_URL = "https://www.googleapis.com/urlshortener/v1/url";
    
    @SuppressWarnings("unchecked")
    public static String shortenUrl(@NotNull String longUrl, @NotNull String apiKey) {
        try {
            String json = "{\"longUrl\": \"" + longUrl + "\"}";
            String apiUrl = SHORTENER_URL + "?key=" + apiKey;
    
            HttpPost post = new HttpPost(apiUrl);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(json, "UTF-8"));
    
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpResponse resp = httpClient.execute(post);
            String responseText = EntityUtils.toString(resp.getEntity());
    
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            HashMap<String, String> res = gson.fromJson(responseText, type);
            
            return res.get("id");
        } catch (IOException e) {
            SimpleLog.getLog("Shortener").warn(e);
            return longUrl;
        }
    }
}
