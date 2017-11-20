package com.raxixor.jdaddons.util;

import com.raxixor.jdaddons.command.Command;
import com.raxixor.jdaddons.command.CommandDescription;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.math.BigDecimal;

public final class StringUtil {

    public static int compareStrings(String a, String b) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        return dist.apply(a, b);
    }
    
    public static double similarityRatio(String a, String b, boolean mult) {
        String longer = a, shorter = b;
        if (a.length() < b.length()) {
            longer = b; shorter = a;
        }
    
        int longerLength = longer.length();
        if (longerLength == 0) return mult ? 100.0 : 1.0;
        double calcRatio = (longerLength - compareStrings(longer, shorter)) / (double) longerLength;
        return calcRatio * (mult ? 100.0 : 1.0);
    }
    
    public static double similarityRatio(String a, String b) {
        return similarityRatio(a, b, false);
    }
    
    public static double similarityRatio(String a, Command cmd, boolean mult) {
        return similarityRatio(a, cmd.getName(), mult);
    }
    
    public static double similarityRatio(String a, Command cmd) {
        return similarityRatio(a, cmd.getName());
    }
    
    public static double similarityRatio(Command a, Command b, boolean mult) {
        return similarityRatio(a.getName(), b.getName(), mult);
    }
    
    public static double similarityRatio(Command a, Command b) {
        return similarityRatio(a.getName(), b.getName());
    }
    
    public static String removeCommand(String str, Command cmd) {
        CommandDescription desc = cmd.getDescription();
        for (String tr : desc.triggers()) {
            if (str.startsWith(tr))
                return str.replaceFirst(tr, "").trim();
        }
        return str;
    }
    
    public static BigDecimal similarityRatioBD(String a, String b, boolean mult) {
        return BigDecimal.valueOf(similarityRatio(a, b, mult));
    }
    
    public static BigDecimal similarityRatioBD(String a, String b) {
        return BigDecimal.valueOf(similarityRatio(a, b));
    }
    
    public static BigDecimal similarityRatioBD(String a, Command cmd, boolean mult) {
        return BigDecimal.valueOf(similarityRatio(a, cmd, mult));
    }
    
    public static BigDecimal similarityRatioBD(String a, Command cmd) {
        return BigDecimal.valueOf(similarityRatio(a, cmd));
    }
    
    public static BigDecimal similarityRatioBD(Command a, Command b, boolean mult) {
        return BigDecimal.valueOf(similarityRatio(a, b, mult));
    }
    
    public static BigDecimal similarityRatioBD(Command a, Command b) {
        return BigDecimal.valueOf(similarityRatio(a, b));
    }
}
