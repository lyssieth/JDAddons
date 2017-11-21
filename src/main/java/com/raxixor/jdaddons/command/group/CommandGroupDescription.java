package com.raxixor.jdaddons.command.group;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandGroupDescription {
    
    String groupName();
    
    String prefix();
    
}
