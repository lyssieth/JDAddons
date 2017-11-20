package com.raxixor.jdaddons.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.raxixor.jdaddons.entities.search.GoogleCustomSearch;
import com.raxixor.jdaddons.entities.search.Result;
import net.dv8tion.jda.core.utils.JDALogger;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class GoogleUtil {
    
    private static final Logger log = JDALogger.getLog("Google");
    private final String API_KEY;
    
    private static LocalDateTime dayStart = null;
    private static int curUsage = 0;
    private static GoogleUtil instance;
    
    private GoogleUtil(String apiKey) {
        this.API_KEY = apiKey;
        dayStart = LocalDateTime.now();
    }
    
    public static GoogleUtil getInstance(String apiKey) {
        if (instance == null) {
            instance = new GoogleUtil(apiKey);
        }
        
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(dayStart.plusDays(1))) {
            dayStart = currentTime;
            curUsage = 0;
        }
        return instance;
    }
    
    public static GoogleUtil getInstance() {
        if (instance == null)
            throw new IllegalArgumentException("Please use the other method first.");
        
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(dayStart.plusDays(1))) {
            dayStart = currentTime;
            curUsage = 0;
        }
        return instance;
    }
    
    public Result search(String query, String cx) {
        if (cx == null || cx.isEmpty())
            throw new IllegalArgumentException("Provided CX is null or empty.");
        if (curUsage >= 80)
            throw new IllegalArgumentException("Google Search usage has reached the premature security cap of 80!");
        
        GoogleCustomSearch search = new GoogleCustomSearch(cx, API_KEY);
        curUsage++;
        Result res = search.execute(query);
        if (res == null)
            return null;
        
        return res;
    }
    
    public Result search(String query, String cx, int total) {
        if (cx == null || cx.isEmpty())
            throw new IllegalArgumentException("Provided CX is null or empty.");
        if (curUsage >= 80)
            throw new IllegalArgumentException("Google Search usage has reached the premature security cap of 80!");
        
        GoogleCustomSearch search = new GoogleCustomSearch(cx, API_KEY, total);
        curUsage++;
        Result res = search.execute(query);
        if (res == null)
            return null;
        
        return res;
    }
    
    @SuppressWarnings("unchecked")
    public String shortenUrl(String longUrl) {
        String json = "{\"longUrl\": \"" + longUrl + "\"}";
        String url = "https://www.googleapis.com/urlshortener/v1/url?key=" + API_KEY;
        
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(json, "UTF-8"));
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpResponse resp = client.execute(post);
            String responseText = EntityUtils.toString(resp.getEntity());
            
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            HashMap<String, String> res = gson.fromJson(responseText, type);
            
            return res.get("id");
        } catch (IOException e) {
            log.warn("", e);
            return longUrl;
        }
    }
}
