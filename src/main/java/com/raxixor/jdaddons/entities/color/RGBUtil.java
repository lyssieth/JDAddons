package com.raxixor.jdaddons.entities.color;

public class RGBUtil {
    
    public static int packRGB(int[] rgb) {
        if (rgb.length != 3)
            throw new IllegalArgumentException("RGB Array should contain exactly 3 values.");
        return rgb[0] << 16 | rgb[1] << 8 | rgb[2];
    }
    
    public static int[] unpackRGB(int packed) {
        int[] rgb = new int[3];
        rgb[0] = packed >> 16 & 0xFF;
        rgb[1] = packed >> 8 & 0xFF;
        rgb[2] = packed & 0xFF;
        return rgb;
    }
    
    public static int[] packRGBArray(int[][] array) {
        int[] packedArray = new int[array.length];
        for (int n = 0; n < array.length; ++n)
            packedArray[n] = packRGB(array[n]);
        return packedArray;
    }
    
    public static int[][] unpackRGBArray(int[] packed) {
        int[][] rgbArray = new int[packed.length][3];
        for (int n = 0; n < packed.length; ++n)
            rgbArray[n] = unpackRGB(packed[n]);
        return rgbArray;
    }
}
