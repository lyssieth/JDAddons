package com.raxixor.jdaddons.command;

import com.raxixor.jdaddons.command.group.*;
import com.raxixor.jdaddons.command.single.Command;
import com.raxixor.jdaddons.command.single.CommandDescription;
import com.raxixor.jdaddons.entities.EmoteLevel;
import com.raxixor.jdaddons.util.FormatUtil;
import com.raxixor.simplelog.SimpleLog;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.apache.commons.text.StrSubstitutor;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("WeakerAccess")
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
    
    /**
     * Finds the closest Command to a specific trigger.
     *
     * @param trigger The trigger to use in the search
     * @return The closest Command, or null, if none are registered or found
     */
    public Command findClosestCommand(String trigger) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        AtomicInteger sd = new AtomicInteger(9999);
        AtomicReference<Command> out = new AtomicReference<>(null);
        
        for (Command cmd : this.commands) {
            CommandDescription desc = cmd.getDescription();
            
            Arrays.stream(desc.triggers()).forEach(ca -> {
                int cd = dist.apply(trigger, ca);
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
    
    /**
     * Finds the closest CommandGroup to a specific name.
     *
     * @param name The name to use in the search
     * @return The closest CommandGroup, or null, if none are registered or found
     */
    public CommandGroup findClosestCommandGroup(String name) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        int sd = 9999;
        AtomicReference<CommandGroup> out = new AtomicReference<>(null);
        
        for (CommandGroup group : commandGroups) {
            int cd = dist.apply(name, group.getDescription().groupName());
            if (cd == 0) return group;
            if (cd < sd) {
                sd = cd;
                out.set(group);
            }
        }
        
        return out.get();
    }
    
    /**
     * Finds the closest CommandGroup to a specific prefix.
     *
     * @param prefix The prefix to use in the search
     * @return The closest CommandGroup, or null, if none are registered or found
     */
    public CommandGroup findClosestCommandGroupPrefix(String prefix) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        int sd = 9999;
        AtomicReference<CommandGroup> out = new AtomicReference<>(null);
        
        for (CommandGroup group : commandGroups) {
            int cd = dist.apply(prefix, group.getDescription().prefix());
            if (cd == 0) return group;
            if (cd < sd) {
                sd = cd;
                out.set(group);
            }
        }
        
        return out.get();
    }
    
    /**
     * Finds the closest SubCommand to a specific trigger, within a CommandGroup.
     *
     * @param group The CommandGroup to search in
     * @param trigger The trigger to use in the search
     * @return The closest SubCommand, or null, if none are registered or found
     */
    public SubCommand findClosestSubCommand(CommandGroup group, String trigger) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        AtomicInteger sd = new AtomicInteger(9999);
        AtomicReference<SubCommand> out = new AtomicReference<>(null);
        
        for (SubCommand cmd : group.getSubCommands()) {
            SubCommandDescription desc = cmd.getDescription();
            
            Arrays.stream(desc.triggers()).forEach(ca -> {
                int cd = dist.apply(trigger, ca);
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
    
    /**
     * Attempts to find a Command from a trigger.
     *
     * @param trigger The trigger to use in the search
     * @return The Command, if it was found, or null
     */
    public Command findCommand(String trigger) {
        return commands.stream().filter(cd -> Arrays.asList(
                cd.getDescription().triggers()).contains(trigger))
                .findFirst().orElse(null);
    }
    
    /**
     * Attempts to find a CommandGroup from a name.
     *
     * @param name Name to use in the search
     * @return The CommandGroup, if it was found, or null
     */
    public CommandGroup findCommandGroup(String name) {
        return commandGroups.stream().filter(cd ->
                cd.getDescription().groupName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
    
    /**
     * Attempts to find a CommandGroup from a SubCommand.
     *
     * @param cmd The SubCommand to use in the search
     * @return The CommandGroup, if it was found, or null
     */
    public CommandGroup findCommandGroup(SubCommand cmd) {
        return commandGroups.stream().filter(cd ->
                cd.getSubCommands().contains(cmd))
                .findFirst().orElse(null);
    }
    
    /**
     * Attempts to find a CommandGroup from a prefix.
     *
     * @param prefix The prefix to use in the search
     * @return The CommandGroup, if it was found, or null
     */
    public CommandGroup findCommandGroupPrefix(String prefix) {
        return commandGroups.stream().filter(cd ->
                cd.getDescription().prefix().equals(prefix))
                .findFirst().orElse(null);
    }
    
    /**
     * Attempts to find a SubCommand from a trigger.
     *
     * @param trigger Trigger to use in the search
     * @return The SubCommand, if it was found, or null
     */
    public SubCommand findSubCommand(String trigger) {
        for (CommandGroup group : commandGroups) {
            if (group.hasSubCommand(trigger))
                return group.getSubCommand(trigger);
        }
        
        return null;
    }
    
    /**
     * Executes a CommandGroup, and attempts to find the SubCommand.
     *
     * @param group The CommandGroup to be executed
     * @param msg The triggering Message
     * @param content Content of the message, minus the prefix
     */
    public void execute(CommandGroup group, Message msg, String content) {
        CommandGroupDescription cgd = group.getDescription();
        
        if (cgd == null) return;
        
        String first = content.split("\\s+")[0].trim();
        content = content.replaceFirst(first, "").trim();
        
        List<String> preArgs = new ArrayList<>(Arrays.asList(content.split("\\s+")));
        
        log.debug(first);
        if (!group.hasSubCommand(first)) {
            log.debug("-- SubCommand not found");
            Map<String, String> val = new HashMap<>();
            val.put("g", group.getDescription().groupName());
            val.put("s", first);
            
            StrSubstitutor sub = new StrSubstitutor(val);
            replyError(msg.getChannel(),
                    sub.replace("Could not find the SubCommand `${s}` in the CommandGroup `${g}`."));
            return;
        }
        log.debug("-- SubCommand found");
        
        SubCommand cmd = group.getSubCommand(first);
        
        if (cmd == null) {
            log.debug("-- SubCommand is null! Something went wrong, please investigate.");
            replyError(msg.getChannel(), "Something went wrong. Please contact whoever made me.");
            return;
        }
        SubCommandDescription scd = cmd.getDescription();
        
        if (!scd.respondToBots() && msg.getAuthor().isBot()) {
            log.debug("Author is bot and the SubCommand doesn't respond to bots");
            return;
        }
        
        boolean argsEmpty = false;
        if (preArgs.size() == 1 && (preArgs.get(0) == null || preArgs.get(0).isEmpty())) {
            preArgs.clear();
            argsEmpty = true;
        }
        
        List<String> args = argsEmpty ? new ArrayList<>() : preArgs;
        
        if (scd.args() > args.size()) {
            HashMap<String, String> val = new HashMap<>();
            val.put("r", (scd.args() + "").trim());
            val.put("s", (args.size() + "").trim());
            
            StrSubstitutor sub = new StrSubstitutor(val);
            replyError(msg.getChannel(),
                    sub.replace("Not enough arguments provided. ${s}/${r}"));
            return;
        }
        
        HashMap<String, String> form = new HashMap<>();
        form.put("user", FormatUtil.formatUser(msg.getAuthor()));
        form.put("chan", FormatUtil.formatChannel(msg.getChannel()));
        form.put("group", cgd.groupName());
        form.put("sub", scd.name());
        
        StrSubstitutor logForm = new StrSubstitutor(form);
        try {
            log.info(logForm.replace("{${user} | ${chan}} Attempting to execute ${group}:${sub}"));
            group.execute(cmd, msg, args);
        } catch (Exception e) {
            log.warn(logForm.replace("{${user} | ${chan}} Could not execute ${group}:${sub}"));
            throw new GroupExecutionException(e);
        }
    }
    
    /**
     * Attempts to find a CommandGroup based on the name, and if successful, executes it.
     *
     * @param name Name of the CommandGroup
     * @param msg Triggering Message
     * @param content Content of the Message, minus the prefix
     */
    public void findAndExecuteGroup(String name, Message msg, String content) {
        CommandGroup group = findCommandGroup(name);
        if (group == null)
            return;
        execute(group, msg, content);
    }
    
    /**
     * Attempts to execute a Command.
     *
     * @param cmd The Command to be executed
     * @param msg The triggering Message
     * @param content The content of the message, minus the Command's trigger
     */
    public void execute(Command cmd, Message msg, String content) {
        CommandDescription cd = cmd.getDescription();
        
        if (cd == null) return;
        
        if (!cd.respondToBots() && msg.getAuthor().isBot()) {
            log.debug("Author is bot and the Command doesn't respond to bots");
            return;
        }
        
        content = content.trim();
        List<String> preArgs = new ArrayList<>(Arrays.asList(content.split("\\s+")));
        
        boolean argsEmpty = false;
        if (preArgs.size() == 1 && (preArgs.get(0) == null || preArgs.get(0).isEmpty())) {
            preArgs.clear();
            argsEmpty = true;
        }
        
        List<String> args = argsEmpty ? new ArrayList<>() : preArgs;
        
        if (cd.args() > args.size()) {
            HashMap<String, String> val = new HashMap<>();
            val.put("c", cd.name());
            val.put("s", (args.size() + "").trim());
            val.put("r", (cd.args() + "").trim());
            
            StrSubstitutor sub = new StrSubstitutor(val);
            replyError(msg.getChannel(),
                    sub.replace("Could not execute Command ${c}, as your supplied argument count (${s}) is " +
                            "lower than the required argument count (${r})."));
            return;
        }
        
        HashMap<String, String> form = new HashMap<>();
        form.put("user", FormatUtil.formatUser(msg.getAuthor()));
        form.put("chan", FormatUtil.formatChannel(msg.getChannel()));
        form.put("cmd", cd.name());
        
        StrSubstitutor logForm = new StrSubstitutor(form);
        try {
            log.info(logForm.replace("{${user} | ${chan}} Executing ${cmd}."));
            cmd.execute(msg, args);
        } catch (Exception e) {
            log.warn(logForm.replace("{${user} | ${chan}} Could not execute ${cmd}."));
            throw new ExecutionException(e);
        }
    }
    
    private void replyError(MessageChannel location, String message) {
        location.sendMessageFormat("%s | %s", EmoteLevel.ERROR,
                message).queue();
    }
}
