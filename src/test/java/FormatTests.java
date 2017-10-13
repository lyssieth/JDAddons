import com.raxixor.jdaddons.entities.ColorType;
import com.raxixor.jdaddons.util.FormatUtil;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.junit.Test;

import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

public class FormatTests {
    
    @Test
    public void testTime() {
        final OffsetDateTime time = OffsetDateTime.of(2002,
                3, 1, 6, 0, 0, 0, ZoneOffset.UTC);
        assertEquals("06:00:00 01.03.2002", FormatUtil.formatTime(time));
    
        final OffsetDateTime timeTwo = OffsetDateTime.of(2002,
                11, 11, 11, 11, 11, 0, ZoneOffset.UTC);
        assertEquals("11:11:11 11.11.2002", FormatUtil.formatTime(timeTwo));
    }
    
    @Test
    public void testFirstToUpper() {
        assertEquals("String", FormatUtil.firstToUpper("string"));
        assertEquals("A", FormatUtil.firstToUpper("a"));
        assertNull(FormatUtil.firstToUpper(""));
    }
    
    @Test
    public void testColor() {
        final Color c = Color.decode("#FF0000");
        assertEquals("#FF0000", FormatUtil.formatColor(c, ColorType.HEX)); // HEX
        assertEquals("255, 0, 0", FormatUtil.formatColor(c, ColorType.RGB)); // RGB
        assertEquals("255, 0, 0, 255", FormatUtil.formatColor(c, ColorType.RGBA)); // RGBA
    }
    
    @Test
    public void testOnlineStatus() {
        assertEquals("Online", FormatUtil.formatOS(OnlineStatus.ONLINE));
        assertEquals("Idle", FormatUtil.formatOS(OnlineStatus.IDLE));
        assertEquals("Do Not Disturb", FormatUtil.formatOS(OnlineStatus.DO_NOT_DISTURB));
        assertEquals("Invisible", FormatUtil.formatOS(OnlineStatus.INVISIBLE));
        assertEquals("Offline", FormatUtil.formatOS(OnlineStatus.OFFLINE));
        assertEquals("Unknown", FormatUtil.formatOS(OnlineStatus.UNKNOWN));
    }
    
    @Test
    public void testRegion() {
        final Region notVip = Region.SYDNEY;
        final Region vip = Region.VIP_SYDNEY;
        assertEquals(notVip.getName(), FormatUtil.formatRegion(notVip));
    }
}
