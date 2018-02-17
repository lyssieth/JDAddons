package com.raxixor.jdaddons.util;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Finds specific things in Strings.
 */
public final class FinderUtil {
    
    private static final Pattern DISCORD_ID = Pattern.compile("\\d{17,20}"); // ID
    private static final Pattern FULL_USER_REF = Pattern.compile("(.{2,32})\\s*#(\\d{17,20})"); // $1 Username, $2 Discriminator
    private static final Pattern USER_MENTION = Pattern.compile("<@!?(\\d{17,20})>"); // $1 ID
    private static final Pattern CHANNEL_MENTION = Pattern.compile("<#(\\d{17,20})>"); // $1 ID
    private static final Pattern ROLE_MENTION = Pattern.compile("<@&(\\d{17,20})>"); // $1 ID
    private static final Pattern EMOTE_MENTION = Pattern.compile("<:(.{2,32}):(\\d{17,20})>"); // $1 Name, $2 ID
    
    public static List<User> findUsers(String query, JDA jda) {
        Matcher userMention = USER_MENTION.matcher(query);
        Matcher fullRefMatch = FULL_USER_REF.matcher(query);
        if (userMention.matches()) {
            User user = jda.getUserById(userMention.group(1));
            if (user != null) return Collections.singletonList(user);
        } else if (fullRefMatch.matches()) {
            String name = fullRefMatch.group(1);
            String discrim = fullRefMatch.group(2);
            List<User> users = jda.getUsers().stream()
                    .filter(user -> user.getName().equalsIgnoreCase(name)
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
        else if (!wrongCase.isEmpty()) return wrongCase;
        else if (!startsWith.isEmpty()) return startsWith;
        else if (!contains.isEmpty()) return contains;
        return null;
    }
    
    public static List<User> findBannedUsers(String query, Guild guild) {
        AtomicReference<List<User>> ret = new AtomicReference<>(null);
        guild.getBanList().queue(bans -> {
        	List<User> banUsers = bans.stream().map(Guild.Ban:: getUser).collect(Collectors.toList());
            Matcher userMention = USER_MENTION.matcher(query);
            Matcher fullRefMatch = FULL_USER_REF.matcher(query);
            if (userMention.matches()) {
                String id = userMention.group(1);
                User user = guild.getJDA().getUserById(id);
                if (user != null && banUsers.contains(user)) {
                    ret.set(Collections.singletonList(user));
                    return;
                }
                for (User u : banUsers)
                    if (u.getId().equalsIgnoreCase(id)) {
                        ret.set(Collections.singletonList(u));
                        return;
                    }
            } else if (fullRefMatch.matches()) {
                String name = fullRefMatch.group(1);
                String discrim = fullRefMatch.group(2);
                List<User> users = guild.getJDA().getUsers().stream()
                        .filter(user -> user.getName().equalsIgnoreCase(name)
                                && user.getDiscriminator().equalsIgnoreCase(discrim)).collect(Collectors.toList());
                if (!users.isEmpty()) {
                    List<User> toRet = new ArrayList<>();
                    for (User u : users)
                        if (banUsers.contains(u))
                            toRet.add(u);
                    
                    if (!toRet.isEmpty()) {
                        ret.set(toRet);
                        return;
                    }
                }
            } else if (DISCORD_ID.matcher(query).matches()) {
                User user = guild.getJDA().getUserById(query);
                if (user != null && banUsers.contains(user)) {
                    ret.set(Collections.singletonList(user));
                    return;
                }
                for (User u : banUsers)
                    if (u.getId().equalsIgnoreCase(query)) {
                        ret.set(Collections.singletonList(user));
                        return;
                    }
            }
            
            ArrayList<User> exact = new ArrayList<>();
            ArrayList<User> wrongCase = new ArrayList<>();
            ArrayList<User> startsWith = new ArrayList<>();
            ArrayList<User> contains = new ArrayList<>();
            
            String lowerQuery = query.toLowerCase();
            for (User u : banUsers) {
                if (u.getName().equals(query)) exact.add(u);
                else if (exact.isEmpty() && u.getName().equalsIgnoreCase(query)) wrongCase.add(u);
                else if (wrongCase.isEmpty() && u.getName().toLowerCase().startsWith(lowerQuery)) startsWith.add(u);
                else if (startsWith.isEmpty() && u.getName().toLowerCase().contains(lowerQuery)) contains.add(u);
            }
            
            if (!exact.isEmpty()) ret.set(exact);
            else if (!wrongCase.isEmpty()) ret.set(wrongCase);
            else if (!startsWith.isEmpty()) ret.set(startsWith);
            else if (!contains.isEmpty()) ret.set(contains);
        });
        
        if (ret.get() != null)
            return ret.get();
        return null;
    }
    
    public static List<Member> findMembers(String query, Guild guild) {
        Matcher userMention = USER_MENTION.matcher(query);
        Matcher fullRefMatch = FULL_USER_REF.matcher(query);
        if (userMention.matches()) {
            Member member = guild.getMemberById(userMention.group(1));
            if (member != null) return Collections.singletonList(member);
        } else if (fullRefMatch.matches()) {
            String name = fullRefMatch.group(1).toLowerCase();
            String discrim = fullRefMatch.group(2).toLowerCase();
            List<Member> members = guild.getMembers().stream()
                    .filter(mem -> mem.getUser().getName().equals(name)
                            && mem.getUser().getDiscriminator().equals(discrim))
                    .collect(Collectors.toList());
            if (!members.isEmpty()) return members;
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
            else if (exact.isEmpty() && (name.equalsIgnoreCase(query) || effName.equalsIgnoreCase(query)))
                wrongCase.add(mem);
            else if (wrongCase.isEmpty() && (name.toLowerCase().startsWith(lowerQuery) || effName.toLowerCase().startsWith(lowerQuery)))
                startsWith.add(mem);
            else if (startsWith.isEmpty() && (name.toLowerCase().contains(lowerQuery) || effName.toLowerCase().contains(lowerQuery)))
                contains.add(mem);
        });
        
        if (!exact.isEmpty()) return exact;
        else if (!wrongCase.isEmpty()) return wrongCase;
        else if (!startsWith.isEmpty()) return startsWith;
        else if (!contains.isEmpty()) return contains;
        return null;
    }
    
    private static List<TextChannel> findTextChannels(String query, List<TextChannel> list) {
        ArrayList<TextChannel> exact = new ArrayList<>();
        ArrayList<TextChannel> wrongCase = new ArrayList<>();
        ArrayList<TextChannel> startsWith = new ArrayList<>();
        ArrayList<TextChannel> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        list.forEach(tc -> {
            String name = tc.getName();
            if (name.equals(query)) exact.add(tc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty()) wrongCase.add(tc);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty()) startsWith.add(tc);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty()) contains.add(tc);
        });
        
        if (!exact.isEmpty()) return exact;
        else if (!wrongCase.isEmpty()) return wrongCase;
        else if (!startsWith.isEmpty()) return startsWith;
        else if (!contains.isEmpty()) return contains;
        return null;
    }
    
    public static List<TextChannel> findTextchannels(String query, JDA jda) {
        Matcher channelMention = CHANNEL_MENTION.matcher(query);
        if (channelMention.matches()) {
            TextChannel tc = jda.getTextChannelById(channelMention.group(1));
            if (tc != null) return Collections.singletonList(tc);
        } else if (DISCORD_ID.matcher(query).matches()) {
            TextChannel tc = jda.getTextChannelById(query);
            if (tc != null) return Collections.singletonList(tc);
        }
        
        return findTextChannels(query, jda.getTextChannels());
    }
    
    public static List<TextChannel> findTextChannels(String query, Guild guild) {
        Matcher channelMention = CHANNEL_MENTION.matcher(query);
        if (channelMention.matches()) {
            TextChannel tc = guild.getTextChannelById(channelMention.group(1));
            if (tc != null) return Collections.singletonList(tc);
        } else if (DISCORD_ID.matcher(query).matches()) {
            TextChannel tc = guild.getTextChannelById(query);
            if (tc != null) return Collections.singletonList(tc);
        }
        
        return findTextChannels(query, guild.getTextChannels());
    }
    
    private static List<VoiceChannel> findVoiceChannels(String query, List<VoiceChannel> list) {
        ArrayList<VoiceChannel> exact = new ArrayList<>();
        ArrayList<VoiceChannel> wrongCase = new ArrayList<>();
        ArrayList<VoiceChannel> startsWith = new ArrayList<>();
        ArrayList<VoiceChannel> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        list.forEach(vc -> {
            String name = vc.getName();
            if (name.equals(query)) exact.add(vc);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty()) wrongCase.add(vc);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty()) startsWith.add(vc);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty()) contains.add(vc);
        });
        
        if (!exact.isEmpty()) return exact;
        else if (!wrongCase.isEmpty()) return wrongCase;
        else if (!startsWith.isEmpty()) return startsWith;
        else if (!contains.isEmpty()) return contains;
        return null;
    }
    
    public static List<VoiceChannel> findVoiceChannels(String query, JDA jda) {
        if (DISCORD_ID.matcher(query).matches()) {
            VoiceChannel vc = jda.getVoiceChannelById(query);
            if (vc != null) return Collections.singletonList(vc);
        }
        
        return findVoiceChannels(query, jda.getVoiceChannels());
    }
    
    public static List<VoiceChannel> findVoiceChannels(String query, Guild guild) {
        if (DISCORD_ID.matcher(query).matches()) {
            VoiceChannel vc = guild.getVoiceChannelById(query);
            if (vc != null) return Collections.singletonList(vc);
        }
        
        return findVoiceChannels(query, guild.getVoiceChannels());
    }
    
    private static List<Category> findCategories(String query, List<Category> list) {
        ArrayList<Category> exact = new ArrayList<>();
        ArrayList<Category> wrongCase = new ArrayList<>();
        ArrayList<Category> startsWith = new ArrayList<>();
        ArrayList<Category> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        list.forEach(cat -> {
            String name = cat.getName();
            if (name.equals(query)) exact.add(cat);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty()) wrongCase.add(cat);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty()) startsWith.add(cat);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty()) contains.add(cat);
        });
        
        if (!exact.isEmpty()) return exact;
        else if (!wrongCase.isEmpty()) return wrongCase;
        else if (!startsWith.isEmpty()) return startsWith;
        else if (!contains.isEmpty()) return contains;
        return null;
    }
    
    public static List<Category> findCategories(String query, JDA jda) {
        if (DISCORD_ID.matcher(query).matches()) {
            Category cat = jda.getCategoryById(query);
            if (cat != null) return Collections.singletonList(cat);
        }
        
        return findCategories(query, jda.getCategories());
    }
    
    public static List<Category> findCategories(String query, Guild guild) {
        if (DISCORD_ID.matcher(query).matches()) {
            Category cat = guild.getCategoryById(query);
            if (cat != null) return Collections.singletonList(cat);
        }
        
        return findCategories(query, guild.getCategories());
    }
    
    private static List<Role> findRoles(String query, List<Role> list) {
        ArrayList<Role> exact = new ArrayList<>();
        ArrayList<Role> wrongCase = new ArrayList<>();
        ArrayList<Role> startsWith = new ArrayList<>();
        ArrayList<Role> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        list.forEach(role -> {
            String name = role.getName();
            if (name.equals(query)) exact.add(role);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty()) wrongCase.add(role);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty()) startsWith.add(role);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty()) contains.add(role);
        });
        
        if (!exact.isEmpty()) return exact;
        else if (!wrongCase.isEmpty()) return wrongCase;
        else if (!startsWith.isEmpty()) return startsWith;
        else if (!contains.isEmpty()) return contains;
        return null;
    }
    
    public static List<Role> findRoles(String query, JDA jda) {
        Matcher roleMention = ROLE_MENTION.matcher(query);
        if (roleMention.matches()) {
            Role role = jda.getRoleById(roleMention.group(1));
            if (role != null) return Collections.singletonList(role);
        } else if (DISCORD_ID.matcher(query).matches()) {
            Role role = jda.getRoleById(query);
            if (role != null) return Collections.singletonList(role);
        }
        
        return findRoles(query, jda.getRoles());
    }
    
    public static List<Role> findRoles(String query, Guild guild) {
        Matcher roleMention = ROLE_MENTION.matcher(query);
        if (roleMention.matches()) {
            Role role = guild.getRoleById(roleMention.group(1));
            if (role != null) return Collections.singletonList(role);
        } else if (DISCORD_ID.matcher(query).matches()) {
            Role role = guild.getRoleById(query);
            if (role != null) return Collections.singletonList(role);
        }
        
        return findRoles(query, guild.getRoles());
    }
    
    private static List<Emote> findEmotes(String query, List<Emote> list) {
        ArrayList<Emote> exact = new ArrayList<>();
        ArrayList<Emote> wrongCase = new ArrayList<>();
        ArrayList<Emote> startsWith = new ArrayList<>();
        ArrayList<Emote> contains = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        list.forEach(emote -> {
            String name = emote.getName();
            if (name.equals(query)) exact.add(emote);
            else if (name.equalsIgnoreCase(query) && exact.isEmpty()) wrongCase.add(emote);
            else if (name.toLowerCase().startsWith(lowerQuery) && wrongCase.isEmpty()) startsWith.add(emote);
            else if (name.toLowerCase().contains(lowerQuery) && startsWith.isEmpty()) contains.add(emote);
        });
        
        if (!exact.isEmpty()) return exact;
        else if (!wrongCase.isEmpty()) return wrongCase;
        else if (!startsWith.isEmpty()) return startsWith;
        else if (!contains.isEmpty()) return contains;
        return null;
    }
    
    public static List<Emote> findEmotes(String query, JDA jda) {
        Matcher emoteMention = EMOTE_MENTION.matcher(query);
        if (emoteMention.matches()) {
            Emote emote = jda.getEmoteById(emoteMention.group(2));
            List<Emote> emotes = jda.getEmotesByName(emoteMention.group(1), false);
            if (emote != null) return Collections.singletonList(emote);
            else if (!emotes.isEmpty()) return emotes;
        } else if (DISCORD_ID.matcher(query).matches()) {
            Emote emote = jda.getEmoteById(query);
            if (emote != null) return Collections.singletonList(emote);
        }
        
        return findEmotes(query, jda.getEmotes());
    }
    
    public static List<Emote> findEmotes(String query, Guild guild) {
        Matcher emoteMention = EMOTE_MENTION.matcher(query);
        if (emoteMention.matches()) {
            Emote emote = guild.getEmoteById(emoteMention.group(2));
            List<Emote> emotes = guild.getEmotesByName(emoteMention.group(1), false);
            if (emote != null) return Collections.singletonList(emote);
            else if (!emotes.isEmpty()) return emotes;
        } else if (DISCORD_ID.matcher(query).matches()) {
            Emote emote = guild.getEmoteById(query);
            if (emote != null) return Collections.singletonList(emote);
        }
        
        return findEmotes(query, guild.getEmotes());
    }
}
