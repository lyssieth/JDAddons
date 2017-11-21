package com.raxixor.jdaddons.command.group;

import com.raxixor.jdaddons.command.single.CommandAttribute;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubCommandDescription {
    
    String name();
    
    String usage();
    
    String[] triggers();
    
    int args() default 0;
    
    boolean respondToBots() default false;
    
    CommandAttribute[] attributes() default { };
}
