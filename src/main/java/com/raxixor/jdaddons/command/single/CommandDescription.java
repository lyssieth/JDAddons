package com.raxixor.jdaddons.command.single;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandDescription {
    
    String name();
    
    String[] triggers();
    
    String usage();
    
    int args() default 0;
    
    boolean respondToBots() default false;
    
    CommandAttribute[] attributes() default { };
}
