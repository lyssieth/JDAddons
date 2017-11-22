package com.raxixor.jdaddons.entities;

public enum EmoteLevel {
    CONFUSED(-2, "\uD83D\uDE15"),
    INFO(-1, "\u00ED"),
    SUCCESS(0, "\u2611"),
    WARNING(1, "\u26A0"),
    ERROR(2, "\u274C"),
    FATAL(3, "\u2620");
    
    private final int value;
    private final String emote;
    EmoteLevel(int value, String emote) {
        this.value = value;
        this.emote = emote;
    }
    
    public int getValue() {
        return value;
    }
    
    public String getEmote() {
        return emote;
    }
    
    @Override
    public String toString() {
        return emote;
    }
}
