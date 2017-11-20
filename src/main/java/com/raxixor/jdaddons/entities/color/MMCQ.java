package com.raxixor.jdaddons.entities.color;

public class MMCQ {
    
    private static final int SIGBITS = 5;
    private static final int RSHIFT = 8 - SIGBITS;
    private static final int MULT = 1 << RSHIFT;
    private static final int HISTOSIZE = 1 << (3 * SIGBITS);
}
