package com.raxixor.jdaddons.util;

import com.raxixor.simplelog.SimpleLog;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Downloads stuff from links.
 */
public class Downloader {
    
    private final static Pattern URL_PATTERN = Pattern.compile("^https://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$");
    private final static Pattern YOUTUBE_PATTERN = Pattern.compile("(?:https://www.youtube.com/watch\\?v=)(.{11})");
    private final static SimpleLog log = SimpleLog.getLog("Downloader");
    
    /**
     * Gets an image from an URL
     *
     * @param url Url to get image from, must be https
     * @return InputStream of the image, or null
     */
    public static InputStream imageFromUrl(String url) {
        if (url == null || url.isEmpty())
            return null;
        
        if (!URL_PATTERN.matcher(url).matches())
            throw new IllegalArgumentException("Provided URL is not a valid URL. (Must be https:// and match the regex)");
        
        try {
            URL u = new URL(url);
            URLConnection connection = u.openConnection();
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/573.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
            return connection.getInputStream();
        } catch (IOException e) {
            log.warn(e);
            return null;
        }
    }
    
    /**
     * Gets the Thumbnail URL for a YouTube video.
     *
     * @param url YouTube video to get Thumbnail URL from
     * @return Thumbnail URL or null
     */
    public static String getYoutubeThumnbnail(String url) {
        if (url == null || url.isEmpty())
            return null;
    
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        if (matcher.matches())
            return "https://img.youtube.com/vi/" + matcher.group(1) + "/0.jpg";
        return null;
    }
    
    /**
     * Attempts to get a BufferedImage from a given URL.
     *
     * @param url URL to get image from.
     * @return BufferedImage, or null if nothing was found.
     */
    public static BufferedImage image(String url) {
        if (url == null || url.isEmpty())
            return null;
        
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            log.warn("Could not find an image at URL: " + url);
            log.warn(e);
            return null;
        }
    }
}
