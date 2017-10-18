import com.raxixor.jdaddons.command.Command;
import com.raxixor.jdaddons.command.CommandAttribute;
import com.raxixor.jdaddons.command.CommandDescription;
import com.raxixor.jdaddons.util.StringUtil;
import net.dv8tion.jda.core.entities.Message;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class StringTests {
    
    @Test
    public void testCompareStrings() {
        assertEquals(0, StringUtil.compareStrings("test", "test"));
        assertEquals(5, StringUtil.compareStrings("aaaaa", "bbbbb"));
    }
    
    @Test
    public void testSimilarityRatio() {
        assertEquals(BigDecimal.valueOf(1.0), StringUtil.similarityRatio("test", "test", false));
        assertEquals(BigDecimal.valueOf(1.0), StringUtil.similarityRatio("test", "test"));
        assertEquals(BigDecimal.valueOf(0.5), StringUtil.similarityRatio("aa", "ab"));
        assertEquals(BigDecimal.valueOf(0.125), StringUtil.similarityRatio("aaaaaaaa", "abbbbbbb"));
        
        assertEquals(BigDecimal.valueOf(100.0), StringUtil.similarityRatio("test", "test", true));
        assertEquals(BigDecimal.valueOf(50.0), StringUtil.similarityRatio("test", "tebb", true));
        
        Command test = new testCommand();
        assertEquals(BigDecimal.valueOf(100.0), StringUtil.similarityRatio(test, test, true));
        assertEquals(BigDecimal.valueOf(100.0), StringUtil.similarityRatio("test", test, true));
    }
    
    @CommandDescription(name = "test", triggers = "test", attributes =
    @CommandAttribute(key = "description", value = "blank"))
    private class testCommand implements Command {
    
        @Override
        public void execute(Message trig, String s) {
        
        }
    }
    
    @Test
    public void testRemovecommand() {
        Command test = new testCommand();
        final String str = "test and test and test!";
        
        assertEquals("and test and test!", StringUtil.removeCommand(str, test));
    }
}
