package com.raxixor.jdaddons.examples;

import com.raxixor.jdaddons.command.Command;
import com.raxixor.jdaddons.command.CommandAttribute;
import com.raxixor.jdaddons.command.CommandDescription;
import net.dv8tion.jda.core.entities.Message;

@CommandDescription(name = "example", triggers = { "example", "ex"}, attributes =
@CommandAttribute(key = "description", value = "Example Description"))
public class ExampleCommand implements Command {
    
    @Override
    public void execute(Message trig, String s) {
    
    }
}
