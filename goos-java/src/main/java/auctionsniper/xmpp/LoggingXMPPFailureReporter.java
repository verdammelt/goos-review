package auctionsniper.xmpp;

import java.util.logging.Logger;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
    private final Logger logger;

    public LoggingXMPPFailureReporter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        StringBuilder builder = new StringBuilder();
        addToBuilderQuotedWith(builder, auctionId, new String[]{"<", ">"});
        builder.append(" Could not translate message ");
        addToBuilderQuotedWith(builder, failedMessage, new String[]{"\"", "\""});
        builder.append(" because ");
        addToBuilderQuotedWith(builder, exception, new String[]{"\"", "\""});
        logger.severe(builder.toString());
    }

    private void addToBuilderQuotedWith(StringBuilder builder, Object thing, String[] quotes) {
        builder.append(quotes[0]);
        builder.append(thing);
        builder.append(quotes[1]);
    }
}
