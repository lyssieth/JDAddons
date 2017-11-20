package com.raxixor.jdaddons.command.group;

import com.raxixor.jdaddons.command.PermissionLevel;
import net.dv8tion.jda.core.entities.Message;

import java.util.List;
import java.util.Set;

public abstract class CommandGroup {
    
    public abstract void execute(SubCommand sCmd, Message trig, List<String> args);
    
    public abstract Set<SubCommand> getSubCommands();
    
    public SubCommand getSubCommand(String trigger) {
        for (SubCommand cmd : getSubCommands()) {
            if (cmd.getTriggers().length > 1) {
                for (String trig : cmd.getTriggers()) {
                    if (trig.equals(trigger))
                        return cmd;
                }
            } else {
                if (trigger.equals(cmd.getTriggers()[0]))
                    return cmd;
            }
        }
        return null;
    }
    
    public CommandGroupDescription getDescription() {
        return getClass().getAnnotation(CommandGroupDescription.class);
    }
    
    public String getGroupName() {
        return getDescription().groupName();
    }
    
    public String getPrefix() {
        return getDescription().prefix();
    }
    
    public PermissionLevel getLevel() {
        return getDescription().level();
    }
    
    public boolean isLevelOverride() {
        return getDescription().levelOverride();
    }
    
    public boolean hasSubCommand(String trigger) {
        for (SubCommand cmd : getSubCommands()) {
            if (cmd.getTriggers().length > 1)
                for (String tr : cmd.getTriggers())
                    if (tr.equals(trigger))
                        return true;
            else
                if (trigger.equals(cmd.getTriggers()[0]))
                    return true;
        }
        return false;
    }
}
