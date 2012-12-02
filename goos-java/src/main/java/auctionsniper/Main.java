package auctionsniper;

import auctionsniper.test.xmpp.XMPPAuction;
import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Main {
    private static MainWindow ui;
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    private final SnipersTableModel snipers = new SnipersTableModel();

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<Auction> notToBeGCCd = new ArrayList<Auction>();

    private Main() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }

    static public void main(String... args) throws Exception {
        Main main = new Main();
        XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME],
                args[ARG_PASSWORD]);
        main.disconnectWhenUiCloses(connection);
        main.addUserRequestListenersFor(connection);
    }

    private void addUserRequestListenersFor(final XMPPConnection connection) {
        ui.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                Auction auction = new XMPPAuction(connection, itemId);
                notToBeGCCd.add(auction);
                auction.addAuctionEventListener(new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId));
                auction.join();
            }
        });
    }

    private void disconnectWhenUiCloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                connection.disconnect();
            }
        });
    }

    private static XMPPConnection connection(
            String hostname, String username, String password)
            throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, XMPPAuction.AUCTION_RESOURCE);
        return connection;
    }

    private static class SwingThreadSniperListener implements SniperListener {
        private final SnipersTableModel snipers;

        public SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }

        @Override
        public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    snipers.snipersStateChanged(sniperSnapshot);
                }
            });
        }
    }
}
