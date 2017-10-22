package com.raxixor.jdaddons.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandDescription {
    String name();
    
    String[] triggers();
    
    int args() default 0;
    
    boolean respondToBots() default false;
    
    CommandAttribute[] attributes() default {};
}
