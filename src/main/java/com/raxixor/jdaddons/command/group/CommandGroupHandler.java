package com.raxixor.jdaddons.command.group;

import com.raxixor.jdaddons.entities.EmoteLevel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.JDALogger;
import org.apache.commons.text.StrSubstitutor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;

import java.util.*;

public abstract class CommandGroupHandler {
    
    private final Logger log = JDALogger.getLog("CommandGroupHandler");
    
    private final Set<CommandGroup> commandGroups = new HashSet<>();
    
    public void registerCommandGroups(Set<CommandGroup> groups) {
        this.commandGroups.addAll(groups);
    }
    
    public void registerCommandGroups(CommandGroup... groups) {
        Collections.addAll(this.commandGroups, groups);
    }
    
    public void registerCommandGroup(CommandGroup group) {
        this.registerCommandGroups(group);
    }
    
    public void unregisterCommandGroups(Set<CommandGroup> groups) {
        this.commandGroups.removeAll(groups);
    }
    
    public void unregisterCommandGroups(CommandGroup... groups) {
        this.commandGroups.removeAll(Arrays.asList(groups));
    }
    
    public void unregisterCommandGroup(CommandGroup group) {
        this.unregisterCommandGroups(group);
    }
    
    public Set<CommandGroup> getCommandGroups() {
        return commandGroups;
    }
    
    public CommandGroup findClosestCommandGroup(String a) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        int sd = 9999;
        CommandGroup finalC = null;
    
        for (CommandGroup group : commandGroups) {
            int cd = dist.apply(a, group.getGroupName());
            if (cd == 0) return group;
            if (cd < sd) {
                sd = cd;
                finalC = group;
            }
        }
        
        return finalC;
    }
    
    public SubCommand findClosestSubCommand(CommandGroup group, String a) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        int sd = 9999;
        SubCommand finalC = null;
        
        for (SubCommand cmd : group.getSubCommands()) {
            if (cmd.getTriggers().length > 1) {
                for (String tr : cmd.getTriggers()) {
                    int cd = dist.apply(a, tr);
                    if (cd == 0) return cmd;
                    if (cd < sd) {
                        sd = cd;
                        finalC = cmd;
                    }
                }
            } else {
                int cd = dist.apply(a, cmd.getTriggers()[0]);
                if (cd == 0) return cmd;
                if (cd < sd) {
                    sd = cd;
                    finalC = cmd;
                }
            }
        }
        
        return finalC;
    }
    
    public CommandGroup findCommandGroup(String s) {
        return commandGroups.stream()
                .filter(cd -> cd.getGroupName().equals(s))
                .findFirst().orElse(null);
    }
    
    public CommandGroup findCommandGroup(SubCommand sCmd) {
        return commandGroups.stream()
                .filter(cd -> cd.getSubCommands().contains(sCmd))
                .findFirst().orElse(null);
    }
    
    public CommandGroup findCommandGroupPrefix(String s) {
        return commandGroups.stream()
                .filter(c -> c.getPrefix().equals(s))
                .findFirst().orElse(null);
    }
    
    public SubCommand findSubCommand(String s) {
        for (CommandGroup group : commandGroups) {
            if (group.hasSubCommand(s))
                return group.getSubCommand(s);
        }
        
        return null;
    }
    
    public void execute(CommandGroup group, Message msg, String s) {
        CommandGroupDescription cgd = group.getDescription();
        
        if (cgd == null) return;
        
        String first = s.split("\\s+")[0];
        s = s.replaceFirst(first, "").trim();
        
        List<String> args = new ArrayList<>(Arrays.asList(s.split("\\s+")));
        
        if (!group.hasSubCommand(first)) {
            Map<String, String> val = new HashMap<>();
            val.put("g", group.getGroupName());
            val.put("s", first);
    
            StrSubstitutor sub = new StrSubstitutor(val);
            String nsficg = "Could not find the sub-command `${s}` in the CommandGroup `${g}`.";
            msg.getChannel().sendMessageFormat("%s | %s",
                    EmoteLevel.ERROR, sub.replace(nsficg)).queue();
            return;
        }
        
        SubCommand sCmd = group.getSubCommand(first);
        
        if (!sCmd.respondToBots() && msg.getAuthor().isBot()) return;
    
        executeStageTwo(group, sCmd, msg, first, args);
    }
    
    public void findAndExecute(String name, Message msg, String s) {
        CommandGroup group = findCommandGroup(name);
        if (group == null)
            return;
        execute(group, msg, s);
    }
    
    public boolean isCommandGroup(CommandGroup group) {
        return (group.getDescription() != null) && (!group.getDescription().prefix().isEmpty());
    }
    
    public abstract void executeStageTwo(CommandGroup group, SubCommand sCmd, Message msg, String first, List<String> args);
}
