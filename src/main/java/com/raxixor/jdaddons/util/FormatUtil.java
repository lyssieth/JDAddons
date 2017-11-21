package com.raxixor.jdaddons.util;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.text.StrSubstitutor;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;

/**
 * Utility for formatting different kinds of things.
 */
@SuppressWarnings("WeakerAccess")
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
        val.put("D", convert.apply(time.getDayOfMonth()));
        val.put("M", convert.apply(time.getMonthValue()));
        val.put("Y", convert.apply(time.getYear()));
        
        StrSubstitutor sub = new StrSubstitutor(val);
        return sub.replace("${h}:${m}:${s} ${D}.${M}.${Y}");
    }
    
    public static String firstToUpper(String str) {
        if (isNullOrEmpty(str)) return null;
        if (str.length() == 1) return str.toUpperCase();
        if (str.length() > 1) return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
        return null;
    }
    
    public static String formatUser(User user) {
        if (user == null) return null;
        return format("%s#%s", user.getName(), user.getDiscriminator());
    }
    
    public static String formatFullUser(User user) {
        if (user == null) return null;
        return format("%s (%s)", formatUser(user), user.getId());
    }
    
    public static String formatMember(Member member) {
        if (member == null) return null;
        if (member.getNickname() != null)
            return format("%s {%s}", formatUser(member.getUser()), member.getNickname());
        else
            return formatUser(member.getUser());
    }
    
    public static String formatFullMember(Member member) {
        if (member == null) return null;
        return format("%s (%s)", formatMember(member), member.getUser().getId());
    }
    
    public static String formatColor(Color color, ColorType type) {
        if (color == null || type == null) return null;
        
        return type.format(color);
    }
    
    public static String formatOS(OnlineStatus os) {
        switch (os) {
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
        switch (level) {
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
        switch (level) {
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
        switch (level) {
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
    
    public static String formatVL(Guild.VerificationLevel level, boolean dongle) {
        if (!dongle)
            return formatVL(level);
        else if (level != Guild.VerificationLevel.VERY_HIGH && level != Guild.VerificationLevel.HIGH)
            return formatVL(level);
        else {
            if (level == Guild.VerificationLevel.VERY_HIGH)
                return "Very High";
            else
                return "High";
        }
    }
    
    public static String formatMFAL(Guild.MFALevel level) {
        switch (level) {
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
        return (region.isVip() ? "VIP - " : "") + region.getName().replace(" (VIP)", "").trim();
    }
    
    public static String formatChannel(MessageChannel channel, boolean mention, boolean id) {
        switch (channel.getType()) {
            case TEXT:
                TextChannel tChan = (TextChannel) channel;
                if (mention) return tChan.getAsMention();
                if (id)
                    return format("#%s (%s)", tChan.getName(), tChan.getId());
                else
                    return format("#%s", tChan.getName());
            case VOICE:
            case CATEGORY:
            default:
                return null;
            case GROUP:
                Group group = (Group) channel;
                if (id)
                    return format("%s (%s)", group.getName(), group.getId());
                else
                    return format("%s", group.getName());
            case PRIVATE:
                PrivateChannel priv = (PrivateChannel) channel;
                if (id)
                    return formatFullUser(priv.getUser());
                else
                    return formatUser(priv.getUser());
            case UNKNOWN:
                return "Unknown";
        }
    }
    
    public static String formatChannel(VoiceChannel channel, boolean id) {
        if (id)
            return format("%s (%s)", channel.getName(), channel.getId());
        else
            return channel.getName();
    }
    
    public static String formatChannel(MessageChannel channel) {
        return formatChannel(channel, false, false);
    }
    
    public static String formatChannel(VoiceChannel channel) {
        return formatChannel(channel, false);
    }
    
    public enum ColorType {
        HEX(0, s -> "#" + Integer.toHexString(s.getRGB()).toUpperCase().substring(2)),
        RGB(1, s -> String.format("%s, %s, %s", s.getRed(), s.getGreen(), s.getBlue())),
        RGBA(2, s -> String.format("%s, %s, %s, %s", s.getRed(), s.getGreen(), s.getBlue(), s.getAlpha()));
        
        private final int type;
        private final Function<Color, String> format;
        ColorType(int type, Function<Color, String> format) {
            this.type = type;
            this.format = format;
        }
    
        public int getType() {
            return type;
        }
    
        public String format(Color color) {
            return format.apply(color);
        }
    
        public static ColorType fromType(int type) {
            switch (type) {
                case 0:
                    return HEX;
                case 1:
                    return RGB;
                case 2:
                    return RGBA;
                default:
                    return null;
            }
        }
        
        @Override
        public String toString() {
            switch (type) {
                case 0:
                    return "HEX";
                case 1:
                    return "RGB";
                case 2:
                    return "RGBA";
                default:
                    return "HEX";
            }
        }
    }
}
