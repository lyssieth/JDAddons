package com.raxixor.jdaddons.util;

import com.raxixor.jdaddons.command.Command;
import com.raxixor.jdaddons.command.CommandDescription;
import com.raxixor.jdaddons.entities.ColorType;
import com.raxixor.jdaddons.entities.Wildcard;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.text.StrSubstitutor;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class FormatUtil {
    
    public static String formatTime(OffsetDateTime time) {
        if (time == null) return null;
    
        Function<Integer, String> convert = s -> {
            if (s <= 9) return "0" + s;
            return "" + s;
        };
    
        Map<String, String> val = new HashMap<>();
        val.put("h", convert.apply(time.getHour()));
        val.put("m", convert.apply(time.getMinute()));
        val.put("s", convert.apply(time.getSecond()));
        val.put("d", convert.apply(time.getDayOfMonth()));
        val.put("n", convert.apply(time.getMonthValue()));
        val.put("y", convert.apply(time.getYear()));
        
        String temp = "${h}:${m}:${s} ${d}.${n}.${y}"; // HH:MM:SS DD.MM.YYYY
        StrSubstitutor sub = new StrSubstitutor(val);
        return sub.replace(temp);
    }
    
    public static String firstToUpper(String str) {
        if (str.length() == 1) return str.toUpperCase();
        if (str.length() > 1) return str.substring(0, 1).toUpperCase() + str.substring(1);
        return null;
    }
    
    public static String formatUser(User usr) {
        if (usr == null) return null;
        return String.format("%s#%s (%s)", usr.getName(), usr.getDiscriminator(), usr.getId());
    }
    
    public static String formatUserWildcard(String str, User user, Wildcard wc) {
        if (user == null || wc == null) return null;
        return wc.replace(str, formatUser(user));
    }
    
    public static String formatUserWildcard(String str, User user, Wildcard wc, boolean mention) {
        if (user == null || wc == null) return null;
        if (!mention) return formatUserWildcard(str, user, wc);
        return wc.replace(str, user.getAsMention());
    }
    
    public static String formatMember(Member mem) {
        if (mem == null) return null;
        User usr = mem.getUser();
        return String.format("%s#%s {%s} (%s)", usr.getName(), usr.getDiscriminator(),
                mem.getNickname() != null ? mem.getNickname() : "No Nickname", usr.getId());
    }
    
    public static String formatMemberWildcard(String str, Member mem, Wildcard wc) {
        if (mem == null || wc == null) return null;
        return wc.replace(str, formatMember(mem));
    }
    
    public static String formatMemberWildcard(String str, Member mem, Wildcard wc, boolean mention) {
        if (mem == null || wc == null) return null;
        if (!mention) return formatMemberWildcard(str, mem, wc);
        return wc.replace(str, mem.getAsMention());
    }
    
    public static String formatColor(Color color, ColorType ty) {
        Function<Color, String> hex = s ->
                "#" + Integer.toHexString(s.getRGB()).toUpperCase().substring(2);
        Function<Color, String> rgb = s ->
                String.format("%s, %s, %s", s.getRed(), s.getGreen(), s.getBlue());
        Function<Color, String> rgba = s ->
                String.format("%s, %s, %s, %s", s.getRed(), s.getGreen(), s.getBlue(),
                        s.getAlpha());
        if (color == null) return null;
        switch(ty) {
            case HEX:
                return hex.apply(color);
            case RGB:
                return rgb.apply(color);
            case RGBA:
                return rgba.apply(color);
            default:
                return null;
        }
    }
    
    public static String formatOS(OnlineStatus os) {
        switch(os) {
            case ONLINE:
                return "Online";
            case IDLE:
                return "Idle";
            case DO_NOT_DISTURB:
                return "Do Not Disturb";
            case INVISIBLE:
                return "Invisible";
            case OFFLINE:
                return "Offline";
            case UNKNOWN:
                return "Unknown";
            default:
                return null;
        }
    }
    
    public static String formatNL(Guild.NotificationLevel level) {
        switch(level) {
            case ALL_MESSAGES:
                return "All Messages";
            case MENTIONS_ONLY:
                return "Mentions Only";
            case UNKNOWN:
                return "Unknown";
            default:
                return null;
        }
    }
    
    public static String formatECL(Guild.ExplicitContentLevel level) {
        switch(level) {
            case ALL:
                return "All";
            case NO_ROLE:
                return "No Role";
            case OFF:
                return "Off";
            case UNKNOWN:
                return "Unknown";
            default:
                return null;
        }
    }
    
    public static String formatVL(Guild.VerificationLevel level) {
        switch(level) {
            case VERY_HIGH:
                return "┻━┻ ﾐヽ(ಠ益ಠ)ノ彡┻━┻";
            case HIGH:
                return "(╯°□°）╯︵ ┻━┻";
            case MEDIUM:
                return "Medium";
            case LOW:
                return "Low";
            case NONE:
                return "None";
            case UNKNOWN:
                return "Unknown";
            default:
                return null;
        }
    }
    
    public static String formatMFAL(Guild.MFALevel level) {
        switch(level) {
            case TWO_FACTOR_AUTH:
                return "2FA Required";
            case NONE:
                return "2FA Not Required";
            case UNKNOWN:
                return "Unknown";
            default:
                return null;
        }
    }
    
    public static String formatRegion(Region region) {
        return (region.isVip() ? "VIP - " : "") + (region.getName().replace(" (VIP)", ""));
    }
    
    public static String formatChannel(MessageChannel chan, boolean mention) {
        switch(chan.getType()) {
            case TEXT:
                TextChannel tChan = (TextChannel) chan;
                if (mention) return tChan.getAsMention();
                return String.format("#%s (%s)", tChan.getName(), tChan.getId());
            case VOICE:
                VoiceChannel vChan = (VoiceChannel) chan;
                return String.format("%s (%s)", vChan.getName(), vChan.getId());
            case PRIVATE:
                PrivateChannel pChan = (PrivateChannel) chan;
                return formatUser(pChan.getUser());
            case GROUP:
                Group group = (Group) chan;
                return String.format("%s (%s)", group.getName(), group.getId());
            case CATEGORY:
                Category cat = (Category) chan;
                return String.format("%s (%s)", cat.getName(), cat.getId());
            default:
                return null;
        }
    }
    
    public static String formatChannel(MessageChannel chan) {
        return formatChannel(chan, false);
    }
    
    public static String formatHelp(Command cmd, String prefix) {
        
        Function<ArrayList<String>, String> gtl = s -> {
            StringBuilder sb = new StringBuilder();
            for (String str : s) {
                sb.append(str).append(", ");
            }
            return sb.substring(0, sb.length() - 2);
        };
        
        CommandDescription desc = cmd.getDescription();
        StringBuilder sb = new StringBuilder();
        sb.append("` ").append(prefix).append(cmd.getName()).append(" {").append(desc.args()).append("} ");
        String[] trigs = desc.triggers();
        ArrayList<String> lst = new ArrayList<>();
        
        for (String trig : trigs) {
            if (trig.equalsIgnoreCase(cmd.getName()))
                continue;
            lst.add(trig);
        }
        
        if (!lst.isEmpty()) {
            sb.append("[").append(gtl.apply(lst)).append("] ");
        }
        
        sb.append(cmd.getAttributeValueFromKey("description"));
        sb.append(" `");
        return sb.toString();
    }
}
