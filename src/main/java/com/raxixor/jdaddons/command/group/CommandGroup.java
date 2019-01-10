package com.raxixor.jdaddons.command.group;

import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandGroup {
    
    private final List<SubCommand> subCommands = new ArrayList<>();
    
    protected void addSubCommand(SubCommand sCmd) {
        if (!subCommands.contains(sCmd))
            subCommands.add(sCmd);
    }
    
    protected void addSubCommands(SubCommand... sCmds) {
        for (SubCommand cmd : sCmds)
            addSubCommand(cmd);
    }
    
    public boolean hasSubCommand(String trigger) {
        for (SubCommand cmd : subCommands) {
            SubCommandDescription desc = cmd.getDescription();
            
            if (desc.name().equals(trigger))
                return true;
            for (String str : desc.triggers()) {
                if (str.equals(trigger))
                    return true;
            }
        }
        
        return false;
    }
    
    public List<SubCommand> getSubCommands() {
        return subCommands;
    }
    
    public SubCommand getSubCommand(String trigger) {
        for (SubCommand cmd : getSubCommands()) {
            SubCommandDescription desc = cmd.getDescription();
            
            if (desc.name().equalsIgnoreCase(trigger))
                return cmd;
            for (String str : desc.triggers())
                if (str.equalsIgnoreCase(trigger))
                    return cmd;
        }
        
        return null;
    }
    
    public CommandGroupDescription getDescription() {
        return getClass().getAnnotation(CommandGroupDescription.class);
    }
    
    public abstract void execute(SubCommand cmd, Message trig, List<String> args);
}
