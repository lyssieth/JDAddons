package com.raxixor.jdaddons.command;

public enum PermissionLevel {
    
    SELF((short) -1),
    OWNER((short) 0),
    CO_OWNER((short) 1),
    ADMIN((short) 2),
    MOD((short) 3),
    TRUSTED((short) 4),
    USER((short) 5),
    MUTED((short) 6);
    
    private final short permLevel;
    PermissionLevel(short permLevel) {
        this.permLevel = permLevel;
    }
    
    public static PermissionLevel getPermissionLevel(short lvl) {
        switch (lvl) {
            case -1:
                return SELF;
            case 0:
                return OWNER;
            case 1:
                return CO_OWNER;
            case 2:
                return ADMIN;
            case 3:
                return MOD;
            case 4:
                return TRUSTED;
            case 5:
                return USER;
            case 6:
                return MUTED;
            default:
                return USER;
        }
    }
    
    public static PermissionLevel getPermissionLevel(long lvl) {
        return getPermissionLevel((short) lvl);
    }
    
    public static boolean isHigherThan(PermissionLevel source, PermissionLevel target) {
        if (source == null && target == null)
            return false;
        if (source == null)
            source = USER;
        if (target == null)
            target = USER;
        
        return source.permLevel < target.permLevel;
    }
    
    public boolean isHigherThan(PermissionLevel target) {
        if (target == null)
            target = USER;
        
        return this.permLevel < target.permLevel;
    }
    
    public static boolean isHigherThanOrEqual(PermissionLevel source, PermissionLevel target) {
        if (source == null && target == null)
            return false;
        if (source == null)
            source = USER;
        if (target == null)
            target = USER;
        
        return source.permLevel <= target.permLevel;
    }
    
    public boolean isHigherThanOrEqual(PermissionLevel target) {
        if (target == null)
            target = USER;
        
        return this.permLevel <= target.permLevel;
    }
    
    public static boolean isLowerThan(PermissionLevel source, PermissionLevel target) {
        if (source == null && target == null)
            return false;
        if (source == null)
            source = USER;
        if (target == null)
            target = USER;
        
        return target.permLevel < source.permLevel;
    }
    
    public boolean isLowerThan(PermissionLevel target) {
        if (target == null)
            target = USER;
        
        return target.permLevel < this.permLevel;
    }
    
    public static boolean isLowerThanOrEqual(PermissionLevel source, PermissionLevel target) {
        if (source == null && target == null)
            return false;
        if (source == null)
            source = USER;
        if (target == null)
            target = USER;
        
        return target.permLevel <= source.permLevel;
    }
    
    public boolean isLowerThanOrEqual(PermissionLevel target) {
        if (target == null)
            target = USER;
        
        return target.permLevel <= this.permLevel;
    }
    
    @Override
    public String toString() {
        switch (this.permLevel) {
            case -1:
                return "Self (-1)";
            case 0:
                return "Owner (0)";
            case 1:
                return "Co-Owner (1)";
            case 2:
                return "Admin (2)";
            case 3:
                return "Moderator (3)";
            case 4:
                return "Trusted (4)";
            case 5:
                return "User (5)";
            case 6:
                return "Muted (6)";
            default:
                return "User (5)";
        }
    }
    
    public short getPermLevel() {
        return permLevel;
    }
}
