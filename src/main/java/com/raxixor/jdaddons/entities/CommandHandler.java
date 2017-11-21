package com.raxixor.jdaddons.entities;

import com.raxixor.jdaddons.command.group.*;
import com.raxixor.jdaddons.command.single.Command;
import com.raxixor.jdaddons.command.single.CommandDescription;
import com.raxixor.simplelog.SimpleLog;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CommandHandler {
    
    private final SimpleLog log = SimpleLog.getLog("CommandHandler");
    
    private final Set<Command> commands = new HashSet<>();
    private final Set<CommandGroup> commandGroups = new HashSet<>();
    
    public void registerCommands(Command... cmds) {
        Collections.addAll(this.commands, cmds);
    }
    
    public void registerCommand(Command cmd) {
        this.registerCommands(cmd);
    }
    
    public void registerCommandGroups(CommandGroup... groups) {
        Collections.addAll(this.commandGroups, groups);
    }
    
    public void registerCommandGroup(CommandGroup group) {
        this.registerCommandGroups(group);
    }
    
    public void unregisterCommands(Command... cmds) {
        this.commands.removeAll(Arrays.asList(cmds));
    }
    
    public void unregisterCommandGroups(CommandGroup... groups) {
        this.commandGroups.removeAll(Arrays.asList(groups));
    }
    
    public void unregisterCommand(Command cmd) {
        this.unregisterCommands(cmd);
    }
    
    public void unregisterCommandGroup(CommandGroup group) {
        this.unregisterCommandGroups(group);
    }
    
    public Set<Command> getCommands() {
        return commands;
    }
    
    public Set<CommandGroup> getCommandGroups() {
        return commandGroups;
    }
    
    public Command findClosestCommand(String a) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        AtomicInteger sd = new AtomicInteger(9999);
        AtomicReference<Command> out = new AtomicReference<>(null);
        
        for (Command cmd : this.commands) {
            CommandDescription desc = cmd.getDescription();
            
            Arrays.stream(desc.triggers()).forEach(ca -> {
                int cd = dist.apply(a, ca);
                if (cd == 0) {
                    out.set(cmd);
                    return;
                }
                if (cd < sd.get()) {
                    sd.set(cd);
                    out.set(cmd);
                }
            });
        }
        
        return out.get();
    }
    
    public CommandGroup findClosestCommandGroup(String a) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        int sd = 9999;
        AtomicReference<CommandGroup> out = new AtomicReference<>(null);
        
        for (CommandGroup group : commandGroups) {
            int cd = dist.apply(a, group.getDescription().groupName());
            if (cd == 0) return group;
            if (cd < sd) {
                sd = cd;
                out.set(group);
            }
        }
        
        return out.get();
    }
    
    public SubCommand findClosestSubCommand(CommandGroup group, String a) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        AtomicInteger sd = new AtomicInteger(9999);
        AtomicReference<SubCommand> out = new AtomicReference<>(null);
        
        for (SubCommand cmd : group.getSubCommands()) {
            SubCommandDescription desc = cmd.getDescription();
            
            Arrays.stream(desc.triggers()).forEach(ca -> {
                int cd = dist.apply(a, ca);
                if (cd == 0) {
                    out.set(cmd);
                    return;
                }
                if (cd < sd.get()) {
                    sd.set(cd);
                    out.set(cmd);
                }
            });
        }
        
        return out.get();
    }
    
    public Command findCommand(String a) {
        return commands.stream().filter(cd -> Arrays.asList(
                cd.getDescription().triggers()).contains(a))
                .findFirst().orElse(null);
    }
    
    public CommandGroup findCommandGroup(String a) {
        return commandGroups.stream().filter(cd ->
                cd.getDescription().groupName().equalsIgnoreCase(a))
                .findFirst().orElse(null);
    }
    
    public CommandGroup findCommandGroup(SubCommand cmd) {
        return commandGroups.stream().filter(cd ->
                cd.getSubCommands().contains(cmd))
                .findFirst().orElse(null);
    }
    
    public CommandGroup findCommandGroupPrefix(String a) {
        return commandGroups.stream().filter(cd ->
                cd.getDescription().prefix().equals(a))
                .findFirst().orElse(null);
    }
    
    public SubCommand findSubCommand(String a) {
        for (CommandGroup group : commandGroups) {
            if (group.hasSubCommand(a))
                return group.getSubCommand(a);
        }
        
        return null;
    }
    
    public void execute(CommandGroup group, Message msg, String content) {
        CommandGroupDescription cgd = group.getDescription();
        
        if (cgd == null) return;
        
        String first = content.split("\\s+")[0];
        content = content.replaceFirst(first, "").trim();
        
        List<String> preArgs = new ArrayList<>(Arrays.asList(content.split("\\s+")));
        
    }
}
