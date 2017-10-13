package com.raxixor.jdaddons.entities;

public enum ColorType {
    HEX(0),
    RGB(1),
    RGBA(2);
    
    private final int type;
    ColorType(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }
    
    @Override
    public String toString() {
        switch(this.type) {
            case 0:
                return "HEX";
            case 1:
                return "RGB";
            case 2:
                return "RGBA";
            default:
                return "HEX";
        }
    }
}
