package com.raxixor.jdaddons.entities;

public enum EmoteLevel {
    
    INFO(-1),
    SUCCESS(0),
    WARNING(1),
    ERROR(2),
    FATAL(3);
    
    private final int value;
    EmoteLevel(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        switch(this.value) {
            case -1:
                return "ℹ"; // INFO
            case 0:
                return "☑"; // SUCCESS
            case 1:
                return "⚠"; // WARNING
            case 2:
                return "❌"; // ERROR
            case 3:
                return "☠"; // FATAL
            default:
                return "ℹ"; // INFO
        }
    }
}
