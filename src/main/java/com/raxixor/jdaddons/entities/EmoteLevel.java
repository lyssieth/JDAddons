package com.raxixor.jdaddons.entities;

public enum EmoteLevel {
    CONFUSED(-2, "\uD83D\uDE15"),
    INFO(-1, "ℹ"),
    SUCCESS(0, "☑"),
    WARNING(1, "⚠"),
    ERROR(2, "❌"),
    FATAL(3, "☠");
    
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
