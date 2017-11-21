package com.raxixor.jdaddons.util;

public final class SafeIdUtil {
    
    public static long safeConvert(String id) {
        try {
            Long l = Long.parseLong(id.trim());
            if (l < 0)
                return 0L;
            return l;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    
    public static boolean checkId(String id) {
        try {
            Long l = Long.parseLong(id.trim());
            return l >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
