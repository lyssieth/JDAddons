package com.raxixor.jdaddons.command;

public @interface CommandAttribute {
    
    String key();
    String value() default "";
}
