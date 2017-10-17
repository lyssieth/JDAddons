import net.dv8tion.jda.core.utils.SimpleLog;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
    
    private static final SimpleLog log = SimpleLog.getLog("Tests");
    
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(
                FormatTests.class,
                SafeIdTests.class,
                StringTests.class
        );
        if (result.wasSuccessful())
            log.info("Success");
        else {
            log.fatal("---------------------");
            for (Failure fail : result.getFailures()) {
                log.fatal(fail);
                log.fatal(fail.getTrace());
                log.fatal("---------------------");
            }
            System.exit(1);
        }
    }
}
