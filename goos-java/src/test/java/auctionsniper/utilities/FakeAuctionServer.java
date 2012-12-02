package auctionsniper.utilities;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static auctionsniper.xmpp.XMPPAuction.BID_COMMAND_FORMAT;
import static auctionsniper.xmpp.XMPPAuction.JOIN_COMMAND_FORMAT;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class FakeAuctionServer {
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String XMPP_HOSTNAME = "virgil.local";
    public static final String AUCTION_RESOURCE = "Auction";
    private static final String AUCTION_PASSWORD = "auction";

    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;
    private final SingleMessageListener messageListener =
            new SingleMessageListener();

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        this.connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public XMPPConnection getConnection() { return connection; }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection
                .login(String.format(ITEM_ID_AS_LOGIN, itemId),
                       AUCTION_PASSWORD, AUCTION_RESOURCE);
        ChatManagerListener chatListener = new ChatManagerListener() {
            @Override
            public void chatCreated(
                    Chat chat, boolean createdLocally) {
                currentChat = chat;
                chat.addMessageListener(messageListener);
            }
        };
        connection.getChatManager().addChatListener(chatListener);
    }

    @SuppressWarnings("SameParameterValue")
    public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(JOIN_COMMAND_FORMAT));
    }

    public void announceClosed() throws XMPPException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE");
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    public void reportPrice(int price, int increment, String bidder)
            throws XMPPException {
        currentChat.sendMessage(String.format(
                "SQLVersion: 1.1; Event: PRICE; " +
                "CurrentPrice: %d; Increment: %d; Bidder: %s;", price,
                increment, bidder));
    }

    @SuppressWarnings("SameParameterValue")
    public void hasReceivedBid(int bid, String sniperId)
            throws InterruptedException {
        receivesAMessageMatching(sniperId,
                                 equalTo(format(BID_COMMAND_FORMAT, bid)));
    }

    private void receivesAMessageMatching(
            String sniperId, Matcher<? super String> messageMatcher)
            throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public void sendInvalidMessageContaining(String brokenMessage) throws XMPPException {
        currentChat.sendMessage(brokenMessage);
    }
}
