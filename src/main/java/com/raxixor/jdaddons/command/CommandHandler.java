package com.raxixor.jdaddons.command;

import com.raxixor.jdaddons.entities.EmoteLevel;
import com.raxixor.jdaddons.util.FormatUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.apache.commons.text.StrSubstitutor;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;

public class CommandHandler {
    
    private final SimpleLog log = SimpleLog.getLog("CommandHandler");
    
    private Set<Command> commands = new HashSet<>();
    
    public void registerCommands(Set<Command> cmds) {
        this.commands.addAll(cmds);
    }
    
    public void registerCommands(Command... cmds) {
        Collections.addAll(this.commands, cmds);
    }
    
    public void registerCommand(Command cmd) {
        this.registerCommands(cmd);
    }
    
    public void unregisterCommands(Set<Command> cmds) {
        this.commands.removeAll(cmds);
    }
    
    public void unregisterCommands(Command... cmds) {
        this.commands.removeAll(Arrays.asList(cmds));
    }
    
    public void unregisterCommand(Command cmd) {
        this.unregisterCommands(cmd);
    }
    
    public Set<Command> getCommands() {
        return this.commands;
    }
    
    public Command findClosestCommand(String a) {
        LevenshteinDistance dist = LevenshteinDistance.getDefaultInstance();
        int shortestDistance = 9999;
        Command finalC = null;
        
        for (Command cmd : this.commands) {
            CommandDescription desc = cmd.getDescription();
            if (desc.triggers().length > 1) {
                for (String tr : desc.triggers()) {
                    int cd = dist.apply(a, tr);
                    if (cd == 0) return cmd;
                    if (cd < shortestDistance) {
                        shortestDistance = cd;
                        finalC = cmd;
                    }
                }
            } else if (desc.triggers().length == 1) {
                int cd = dist.apply(a, desc.triggers()[0]);
                if (cd == 0) return cmd;
                if (cd < shortestDistance) {
                    shortestDistance = cd;
                    finalC = cmd;
                }
            }
        }
        
        return finalC;
    }
    
    public Command findCommand(String s) {
        return this.commands.stream().filter(cd -> Arrays.asList(cd.getDescription().triggers())
                .contains(s)).findFirst().orElse(null);
    }
    
    public void execute(Command cmd, Message msg, String args) {
        CommandDescription cd = cmd.getDescription();
        String NOT_ENOUGH_ARGS = "Could not execute command ${cmd}, as your supplied argument amount (${sup}) is " +
                "lower than the required argument count (${req}).";
        Map<String, String> val = new HashMap<>();
        if (cd == null) return;
        args = args.trim();
        if (cd.args() > args.split("\\s+").length) {
            val.put("cmd", cmd.getName());
            val.put("sup", args.split("\\s+").length + "");
            val.put("req", cd.args() + "");
            StrSubstitutor sub = new StrSubstitutor(val);
            msg.getChannel().sendMessageFormat("%s | %s",
                    EmoteLevel.ERROR, sub.replace(NOT_ENOUGH_ARGS)).queue();
        } else {
            try {
                log.info(String.format("{%s %s} Executing %s.", FormatUtil.formatUser(msg.getAuthor()),
                        FormatUtil.formatChannel(msg.getChannel()), cmd.getName()));
                cmd.execute(msg, args);
            } catch (Exception e) {
                log.warn(String.format("{%s %s} Could not execute %s.", FormatUtil.formatUser(msg.getAuthor()),
                        FormatUtil.formatChannel(msg.getChannel()), cmd.getName()));
                throw new ExecutionException(e);
            }
        }
    }
    
    public void findAndExecute(String trigger, Message msg, String args) {
        Command cmd = this.findCommand(trigger);
        if (cmd == null || cmd.getDescription() == null)
            return;
        this.execute(cmd, msg, args);
    }
    
    public boolean isCommand(Command cmd) {
        return cmd.getDescription() != null && cmd.hasAttribute("description");
    }
}
