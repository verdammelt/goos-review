package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuctionHouse;

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
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUiCloses(auctionHouse);
        main.addUserRequestListenersFor(auctionHouse);
    }

    private void addUserRequestListenersFor(final XMPPAuctionHouse auctionHouse) {
        ui.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                Auction auction = auctionHouse.auctionFor(itemId);
                notToBeGCCd.add(auction);
                auction.addAuctionEventListener(new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId));
                auction.join();
            }
        });
    }

    private void disconnectWhenUiCloses(final XMPPAuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                auctionHouse.disconnect();
            }
        });
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
