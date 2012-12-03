package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.lib.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static java.lang.String.format;

public class  XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT =
            "SOLVersion: 1.1; Event: JOIN;";
    public static final String BID_COMMAND_FORMAT =
            "SOLVersion: 1.1; Event: BID; Price: %d;";

    public static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT =
            ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private final Chat chat;
    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

    public XMPPAuction(XMPPConnection connection, String itemId) {
        AuctionMessageTranslator translator = translatorFor(connection);
        this.chat = connection.getChatManager()
                .createChat(auctionId(itemId, connection), translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), null);
    }

    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
        return new AuctionEventListener() {
            @Override public void auctionFailed() { chat.removeMessageListener(translator); }
            @Override public void auctionClosed() { }
            @Override public void currentPrice(int price, int increment, PriceSource fromOtherBidder) { }
        };
    }

    private String auctionId(String itemId, XMPPConnection connection) {
        return format(AUCTION_ID_FORMAT, itemId, connection
                .getServiceName());
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }

    @Override
    public void bid(int amount) {
        sendMessage(format(BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}

