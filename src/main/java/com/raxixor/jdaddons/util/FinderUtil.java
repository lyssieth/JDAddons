package com.raxixor.jdaddons.util;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public final class FinderUtil {
    
    private final static Pattern DISCORD_ID = Pattern.compile("\\d{17,20}"); // ID
    private final static Pattern FULL_USER_REF = Pattern.compile("(.{2,32})\\s*#(\\d{4})"); // $1 Username, $2 Discriminator
    private final static Pattern USER_MENTION = Pattern.compile("<@!?(\\d{17,20})>"); // $1 ID
    private final static Pattern CHANNEL_MENTION = Pattern.compile("<#(\\d{17,20})>"); // $1 ID
    private final static Pattern ROLE_MENTION = Pattern.compile("<@&(\\d{17,20})>"); // $1 ID
    private final static Pattern EMOTE_MENTION = Pattern.compile("<:(.{2,32}):(\\d{17,20})>"); // $1 Name, $2 ID
    
    public static List<User> findUsers(String query, JDA jda) {
        Matcher userMention = USER_MENTION.matcher(query);
        Matcher fullRefMatch = FULL_USER_REF.matcher(query);
        if (userMention.matches()) {
            User user = jda.getUserById(userMention.group(1));
            if (user != null) return Collections.singletonList(user);
        } else if (fullRefMatch.matches()) {
            String lowerName = fullRefMatch.group(1).toLowerCase();
            String discrim = fullRefMatch.group(2);
            List<User> users = jda.getUsers().stream()
                    .filter(user -> user.getName().toLowerCase().equals(lowerName)
                            && user.getDiscriminator().equals(discrim))
                    .collect(Collectors.toList());
            if (!users.isEmpty()) return users;
        } else if (DISCORD_ID.matcher(query).matches()) {
            User user = jda.getUserById(query);
            if (user != null) return Collections.singletonList(user);
        }
        ArrayList<User> exact = new ArrayList<>();
        ArrayList<User> wrongCase = new ArrayList<>();
        ArrayList<User> startsWith = new ArrayList<>();
        ArrayList<User> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        jda.getUsers().forEach(user -> {
            String name = user.getName();
            if (name.equals(query)) exact.add(user);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty()) wrongCase.add(user);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty()) startsWith.add(user);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty()) contains.add(user);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<User> findBannedUsers(String query, Guild guild) {
        List<User> bans;
        try {
            bans = guild.getBans().complete();
        } catch (Exception e) {
            return null;
        }
        
        String discrim = null;
        Matcher userMention = USER_MENTION.matcher(query);
        if (userMention.matches()) {
            String id = userMention.group(1);
            User user = guild.getJDA().getUserById(id);
            if (user != null && bans.contains(user))
                return Collections.singletonList(user);
            for (User u : bans)
                if (u.getId().equalsIgnoreCase(id))
                    return Collections.singletonList(u);
        } else if (FULL_USER_REF.matcher(query).matches()) {
            discrim = query.substring(query.length() - 4);
            query = query.substring(0, query.length() - 5).trim();
        } else if (DISCORD_ID.matcher(query).matches()) {
            User user = guild.getJDA().getUserById(query);
            if (user != null && bans.contains(user))
                return Collections.singletonList(user);
            for (User u : bans)
                if (u.getId().equalsIgnoreCase(query))
                    return Collections.singletonList(u);
        }
        ArrayList<User> exact = new ArrayList<>();
        ArrayList<User> wrongCase = new ArrayList<>();
        ArrayList<User> startsWith = new ArrayList<>();
        ArrayList<User> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (User u : bans) {
            if (discrim != null && !u.getDiscriminator().equals(discrim))
                continue;
            
            if (u.getName().equals(query))
                exact.add(u);
            else if (exact.isEmpty() && u.getName().equalsIgnoreCase(query))
                wrongCase.add(u);
            else if (wrongCase.isEmpty() && u.getName().toLowerCase().startsWith(lowerQuery))
                startsWith.add(u);
            else if (startsWith.isEmpty() && u.getName().toLowerCase().contains(lowerQuery))
                contains.add(u);
        }
        
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<Member> findMembers(String query, Guild guild) {
        Matcher userMention = USER_MENTION.matcher(query);
        Matcher fullRefMatch = FULL_USER_REF.matcher(query);
        if (userMention.matches()) {
            Member member = guild.getMemberById(userMention.group(1));
            if (member != null)
                return Collections.singletonList(member);
        } else if (fullRefMatch.matches()) {
            String lowerName = fullRefMatch.group(1).toLowerCase();
            String discrim = fullRefMatch.group(2);
            List<Member> members = guild.getMembers().stream()
                    .filter(mem -> mem.getUser().getName().toLowerCase().equals(lowerName)
                            && mem.getUser().getDiscriminator().equals(discrim))
                    .collect(Collectors.toList());
            if (!members.isEmpty())
                return members;
        } else if (DISCORD_ID.matcher(query).matches()) {
            Member member = guild.getMemberById(query);
            if (member != null)
                return Collections.singletonList(member);
        }
        
        ArrayList<Member> exact = new ArrayList<>();
        ArrayList<Member> wrongCase = new ArrayList<>();
        ArrayList<Member> startsWith = new ArrayList<>();
        ArrayList<Member> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        guild.getMembers().forEach(mem -> {
            String name = mem.getUser().getName();
            String effName = mem.getEffectiveName();
            if (name.equals(query) || effName.equals(query))
                exact.add(mem);
            else if ((name.equalsIgnoreCase(query) || effName.equalsIgnoreCase(query)) && exact.isEmpty())
                wrongCase.add(mem);
            else if ((name.toLowerCase().startsWith(lowerQuery) || effName.toLowerCase().startsWith(query))
                    && wrongCase.isEmpty())
                startsWith.add(mem);
            else if ((name.toLowerCase().contains(lowerQuery) || effName.toLowerCase().contains(lowerQuery))
                    && startsWith.isEmpty())
                contains.add(mem);
        });
        
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<TextChannel> findTextChannels(String query, JDA jda) {
        Matcher channelMention = CHANNEL_MENTION.matcher(query);
        if (channelMention.matches()) {
            TextChannel tc = jda.getTextChannelById(channelMention.group(1));
            if (tc != null)
                return Collections.singletonList(tc);
        } else if (DISCORD_ID.matcher(query).matches()) {
            TextChannel tc = jda.getTextChannelById(query);
            if (tc != null)
                return Collections.singletonList(tc);
        }
        ArrayList<TextChannel> exact = new ArrayList<>();
        ArrayList<TextChannel> wrongCase = new ArrayList<>();
        ArrayList<TextChannel> startsWith = new ArrayList<>();
        ArrayList<TextChannel> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        jda.getTextChannels().forEach(tc -> {
            String name = tc.getName();
            if (name.equals(query))
                exact.add(tc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(tc);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty())
                startsWith.add(tc);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty())
                contains.add(tc);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<TextChannel> findTextChannels(String query, Guild guild) {
        Matcher channelMention = CHANNEL_MENTION.matcher(query);
        if (channelMention.matches()) {
            TextChannel tc = guild.getTextChannelById(channelMention.group(1));
            if (tc != null)
                return Collections.singletonList(tc);
        } else if (DISCORD_ID.matcher(query).matches()) {
            TextChannel tc = guild.getTextChannelById(query);
            if (tc != null)
                return Collections.singletonList(tc);
        }
        ArrayList<TextChannel> exact = new ArrayList<>();
        ArrayList<TextChannel> wrongCase = new ArrayList<>();
        ArrayList<TextChannel> startsWith = new ArrayList<>();
        ArrayList<TextChannel> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        guild.getTextChannels().forEach(tc -> {
            String name = tc.getName();
            if (name.equals(query))
                exact.add(tc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(tc);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(tc);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(tc);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<VoiceChannel> findVoiceChannels(String query, JDA jda) {
        if (DISCORD_ID.matcher(query).matches()) {
            VoiceChannel vc = jda.getVoiceChannelById(query);
            if (vc != null)
                return Collections.singletonList(vc);
        }
        ArrayList<VoiceChannel> exact = new ArrayList<>();
        ArrayList<VoiceChannel> wrongCase = new ArrayList<>();
        ArrayList<VoiceChannel> startsWith = new ArrayList<>();
        ArrayList<VoiceChannel> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        jda.getVoiceChannels().forEach(vc -> {
            String name = vc.getName();
            if (name.equals(query))
                exact.add(vc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(vc);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(vc);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(vc);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<VoiceChannel> findVoiceChannels(String query, Guild guild) {
        if (DISCORD_ID.matcher(query).matches()) {
            VoiceChannel vc = guild.getVoiceChannelById(query);
            if (vc != null)
                return Collections.singletonList(vc);
        }
        ArrayList<VoiceChannel> exact = new ArrayList<>();
        ArrayList<VoiceChannel> wrongCase = new ArrayList<>();
        ArrayList<VoiceChannel> startsWith = new ArrayList<>();
        ArrayList<VoiceChannel> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        guild.getVoiceChannels().forEach(vc -> {
            String name = vc.getName();
            if (name.equals(query))
                exact.add(vc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(vc);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(vc);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(vc);
        });
        
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<Category> findCategories(String query, JDA jda) {
        if (DISCORD_ID.matcher(query).matches()) {
            Category cat = jda.getCategoryById(query);
            if (cat != null)
                return Collections.singletonList(cat);
        }
        ArrayList<Category> exact = new ArrayList<>();
        ArrayList<Category> wrongCase = new ArrayList<>();
        ArrayList<Category> startsWith = new ArrayList<>();
        ArrayList<Category> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        jda.getCategories().forEach(cat -> {
            String name = cat.getName();
            if (name.equals(query))
                exact.add(cat);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(cat);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(cat);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(cat);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<Category> findCategories(String query, Guild guild) {
        if (DISCORD_ID.matcher(query).matches()) {
            Category cat = guild.getCategoryById(query);
            if (cat != null)
                return Collections.singletonList(cat);
        }
        ArrayList<Category> exact = new ArrayList<>();
        ArrayList<Category> wrongCase = new ArrayList<>();
        ArrayList<Category> startsWith = new ArrayList<>();
        ArrayList<Category> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        guild.getCategories().forEach(cat -> {
            String name = cat.getName();
            if (name.equals(query))
                exact.add(cat);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(cat);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(cat);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(cat);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<Role> findRoles(String query, Guild guild) {
        Matcher roleMention = ROLE_MENTION.matcher(query);
        if (roleMention.matches()) {
            Role role = guild.getRoleById(roleMention.group(1));
            if (role != null)
                return Collections.singletonList(role);
        } else if (DISCORD_ID.matcher(query).matches()) {
            Role role = guild.getRoleById(query);
            if (role != null)
                return Collections.singletonList(role);
        }
        ArrayList<Role> exact = new ArrayList<>();
        ArrayList<Role> wrongCase = new ArrayList<>();
        ArrayList<Role> startsWith = new ArrayList<>();
        ArrayList<Role> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        List<Role> guildRoles = new ArrayList<>(guild.getRoles());
        guildRoles.add(guild.getPublicRole());
        guildRoles.forEach(role -> {
            String name = role.getName();
            if (name.equals(query))
                exact.add(role);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(role);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(role);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(role);
        });
        
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<Emote> findEmotes(String query, JDA jda) {
        Matcher emoteMention = EMOTE_MENTION.matcher(query);
        if (emoteMention.matches()) {
            Emote emote = jda.getEmoteById(emoteMention.group(2));
            List<Emote> emotes = jda.getEmotesByName(emoteMention.group(1), false);
            if (emote != null)
                return Collections.singletonList(emote);
            if (!emotes.isEmpty())
                return emotes;
        } else if (DISCORD_ID.matcher(query).matches()) {
            Emote emote = jda.getEmoteById(query);
            if (emote != null)
                return Collections.singletonList(emote);
        }
        ArrayList<Emote> exact = new ArrayList<>();
        ArrayList<Emote> wrongCase = new ArrayList<>();
        ArrayList<Emote> startsWith = new ArrayList<>();
        ArrayList<Emote> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        jda.getEmotes().forEach(emote -> {
            String name = emote.getName();
            if (name.equals(query))
                exact.add(emote);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(emote);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(emote);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(emote);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
    
    public static List<Emote> findEmotes(String query, Guild guild) {
        Matcher emoteMention = EMOTE_MENTION.matcher(query);
        if (emoteMention.matches()) {
            Emote emote = guild.getEmoteById(emoteMention.group(2));
            List<Emote> emotes = guild.getEmotesByName(emoteMention.group(1), false);
            if (emote != null)
                return Collections.singletonList(emote);
            if (!emotes.isEmpty())
                return emotes;
        } else if (DISCORD_ID.matcher(query).matches()) {
            Emote emote = guild.getEmoteById(query);
            if (emote != null)
                return Collections.singletonList(emote);
        }
        ArrayList<Emote> exact = new ArrayList<>();
        ArrayList<Emote> wrongCase = new ArrayList<>();
        ArrayList<Emote> startsWith = new ArrayList<>();
        ArrayList<Emote> contains = new ArrayList<>();
        String lq = query.toLowerCase();
        guild.getEmotes().forEach(emote -> {
            String name = emote.getName();
            if (name.equals(query))
                exact.add(emote);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty())
                wrongCase.add(emote);
            else if (name.toLowerCase().startsWith(lq) && wrongCase.isEmpty())
                startsWith.add(emote);
            else if (name.toLowerCase().contains(lq) && startsWith.isEmpty())
                contains.add(emote);
        });
        if (!exact.isEmpty()) return exact;
        if (!wrongCase.isEmpty()) return wrongCase;
        if (!startsWith.isEmpty()) return startsWith;
        return contains;
    }
}
