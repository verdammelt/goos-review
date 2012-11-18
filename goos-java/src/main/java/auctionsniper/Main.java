package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.String.format;

public class Main {
    private static MainWindow ui;
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT =
            ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String JOIN_COMMAND_FORMAT = null;
    public static final String BID_COMMAND_FORMAT =
            "SOLVersion: 1.1; Command BID; Price: %d;";

    private final SnipersTableModel snipers = new SnipersTableModel();

    @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
    private Chat notToBeGCCd;

    public Main() throws Exception {
        startUserInterface();
    }

    static public void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(connection(args[ARG_HOSTNAME], args[ARG_USERNAME],
                                    args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
    }

    private void joinAuction(XMPPConnection connection, String itemId)
            throws XMPPException {
        disconnectWhenUiCloses(connection);

        final Chat chat = connection.getChatManager()
                                    .createChat(auctionId(itemId,
                                                          connection), null);
        this.notToBeGCCd = chat;

        Auction auction = new XMPPAuction(chat);
        AuctionMessageTranslator listener =
                new AuctionMessageTranslator(connection.getUser(),
                        new AuctionSniper(auction,
                                new SwingThreadSniperListener(snipers),
                                itemId));
        chat.addMessageListener(listener);

        auction.join();
    }

    private void disconnectWhenUiCloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent windowEvent) {
                connection.disconnect();
            }
        });
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection
                .getServiceName());
    }

    private static XMPPConnection connection(
            String hostname, String username, String password)
            throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }

    public static class XMPPAuction implements Auction {
        private Chat chat;

        public XMPPAuction(Chat chat) {
            this.chat = chat;
        }

        @Override public void bid(int amount) {
            sendMessage(format(BID_COMMAND_FORMAT, amount));
        }

        @Override public void join() {
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

    public class SwingThreadSniperListener implements SniperListener {
        private final SnipersTableModel snipers;

        public SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }

        @Override public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    snipers.snipersStateChanged(sniperSnapshot);
                }
            });
        }
    }
}
