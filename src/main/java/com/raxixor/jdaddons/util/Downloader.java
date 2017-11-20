package com.raxixor.jdaddons.util;

import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Downloader {
    
    private final static Pattern URL_PATTERN = Pattern.compile("^https://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$");
    private final static Pattern YOUTUBE_PATTERN = Pattern.compile("(?:https://www.youtube.com/watch\\?v=)(.{11})");
    private final static Logger log = JDALogger.getLog("Downloader");
    
    public static InputStream imageFromUrl(String url) {
        if (url == null || url.isEmpty())
            return null;
        
        if (!URL_PATTERN.matcher(url).matches())
            throw new IllegalArgumentException("Provided URL is not a valid URL. (Must be https:// and match the regex.)");
        
        try {
            URL u = new URL(url);
            URLConnection connection = u.openConnection();
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/573.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
            return connection.getInputStream();
        } catch (IOException e) {
            log.warn("", e);
            return null;
        }
    }
    
    public static String getYouTubeThumbnail(String url) {
        if (url == null || url.isEmpty())
            return null;
    
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        if (matcher.matches())
            return "https://img.youtube.com/vi/" + matcher.group(1) + "/0.jpg";
        return null;
    }
    
    public static String webPage(String urlText) {
        if (urlText == null || urlText.isEmpty())
            return null;
        StringBuilder webPageText = new StringBuilder();
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        
        try {
            url = new URL(urlText);
            is = url.openStream();
            br = new BufferedReader(new InputStreamReader(is));
            
            while ((line = br.readLine()) != null)
                webPageText.append(line);
        } catch (IOException e) {
            log.warn("", e);
            return null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                log.warn("", e);
            }
        }
        return webPageText.toString();
    }
    
    public static BufferedImage image(String url) {
        if (url == null || url.isEmpty())
            return null;
        
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            log.warn("Could not find an image at URL: " + url);
            log.warn("", e);
            return null;
        }
    }
    
    public static File file(String url, String filename) throws IOException {
        File file = new File(filename);
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();
        
        URL link = new URL(url);
        
        InputStream in = new BufferedInputStream(link.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while (-1 != (n = in.read(buf)))
            out.write(buf, 0, n);
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(response);
        fos.close();
        return file;
    }
}
