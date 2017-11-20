package com.raxixor.jdaddons.command.group;

import com.raxixor.jdaddons.command.PermissionLevel;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandGroupDescription {
    
    String groupName();
    
    String prefix();
    
    PermissionLevel level() default PermissionLevel.USER;
    boolean levelOverride() default false;
}
