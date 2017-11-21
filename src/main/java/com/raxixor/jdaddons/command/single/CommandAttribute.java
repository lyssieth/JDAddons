package com.raxixor.jdaddons.command.single;

public @interface CommandAttribute {
    
    String key();
    String value() default "";
}
