package com.raxixor.jdaddons.command.group;

import com.raxixor.jdaddons.command.CommandAttribute;
import com.raxixor.jdaddons.command.PermissionLevel;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubCommandDescription {
    
    String name();
    String[] triggers();
    
    PermissionLevel level() default PermissionLevel.USER;
    
    int args() default 0;
    
    boolean respondToBots() default false;
    
    CommandAttribute[] attributes() default {};
}
