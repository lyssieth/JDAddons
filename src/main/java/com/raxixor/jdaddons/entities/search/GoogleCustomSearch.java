package com.raxixor.jdaddons.entities.search;

import com.google.gson.Gson;
import com.raxixor.simplelog.SimpleLog;
import org.apache.commons.text.StrSubstitutor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.*;

import static com.google.common.base.CharMatcher.whitespace;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class GoogleCustomSearch {
    
    private String cx;
    private String apiKey;
    private int num;
    private int start = 1;
    private static final SimpleLog log = SimpleLog.getLog("GoogleCustomSearch");
    
    public GoogleCustomSearch(String cx, String apiKey) {
        this(cx, apiKey, 0);
    }
    
    public GoogleCustomSearch(String cx, String apiKey, int num) {
        if (isNullOrEmpty(cx) || isNullOrEmpty(apiKey))
            throw new IllegalArgumentException("CX and API Key are required");
        
        setApiKey(apiKey);
        setCx(cx);
        setNum(num);
    }
    
    private String getUri(String query) {
        HashMap<String, String> val = new HashMap<>();
        val.put("cx", cx);
        val.put("key", apiKey);
        val.put("query", whitespace().trimAndCollapseFrom(query, '+'));
        StrSubstitutor sub = new StrSubstitutor(val);
        return (sub.replace("https://www.googleapis.com/customsearch/v1?key=${key}&cx=${cx}&q=${query}") + "&start=" + getStart() + "&alt=json");
    }
    
    private HttpResponse getResponse(String query) {
        String uri = getUri(query);
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(uri);
            request.addHeader("accept", "application/json");
            
            return client.execute(request);
        } catch (IOException e) {
            log.warn(e);
            return null;
        }
    }
    
    private String getJson(HttpResponse response) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String output;
            String json = "";
            
            while ((output = br.readLine()) != null)
                json = json.concat(output);
            
            return json;
        } catch (IllegalStateException | IOException e) {
            log.warn(e);
            return null;
        }
    }
    
    public Result execute(String query) {
        Result result = getSearchResult(query);
        if (result == null)
            return null;
        
        if (result.getItems().size() < getNum()) {
            Result res = getSearchResult(query);
            if (res == null)
                return null;
            List<Item> items = res.getItems();
            
            for (Item item : items)
                if (result.getItems().size() < getNum())
                    result.getItems().add(item);
        } else {
            result.getItems().subList(getNum(), result.getItems().size()).clear();
        }
        
        return result;
    }
    
    private Result filterItems(Result result) {
        Iterator<Item> items = result.getItems().iterator();
        
        while (items.hasNext())
            if (isBlank(items.next().getFormattedUrl()))
                items.remove();
        
        return result;
    }
    
    private Result getSearchResult(String query) {
        HttpResponse response = getResponse(query);
        if (response == null) return null;
        String json = getJson(response);
        if (json == null) return null;
        
        Queries queries = new Meta(json).getQueries();
        setStart(queries.getNextPage().get(0).getStartIndex());
        
        return filterItems(new Gson().fromJson(json, Result.class));
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public int getNum() {
        return num;
    }
    
    public void setNum(int num) {
        this.num = num;
    }
    
    public String getCx() {
        return cx;
    }
    
    public void setCx(String cx) {
        this.cx = cx;
    }
    
    public int getStart() {
        return start;
    }
    
    public void setStart(int start) {
        this.start = start;
    }
}
