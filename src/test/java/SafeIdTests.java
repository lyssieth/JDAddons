import com.raxixor.jdaddons.util.SafeIdUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class SafeIdTests {
    
    @Test
    public void testConvert() {
        assertEquals(1000, SafeIdUtil.safeConvert("1000"));
        assertEquals(0, SafeIdUtil.safeConvert("-500"));
        assertEquals(0, SafeIdUtil.safeConvert("test"));
    }
    
    @Test
    public void testId() {
        assertEquals(true, SafeIdUtil.checkId("1235"));
        assertEquals(false, SafeIdUtil.checkId("test"));
    }
}
