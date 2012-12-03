package auctionsniper.xmpp;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggingXMPPFailureReporterTest {
    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final Logger logger = context.mock(Logger.class);
    private final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterClass
    public static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    public void writesMessageTranslationFailureToLog() {
        context.checking(new Expectations() {{
            oneOf(logger).severe("<auction id> "
                    + "Could not translate message \"bad message\" "
                    + "because \"java.lang.Exception: bad\"");
        }});
        reporter.cannotTranslateMessage("auction id", "bad message",
                new Exception("bad"));
    }
}
