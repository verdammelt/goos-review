package auctionsniper;

import auctionsniper.ui.SnipersTableModel;

import javax.swing.*;
import java.util.ArrayList;

public class SniperLauncher implements UserRequestListener {
    private SnipersTableModel snipers;
    private AuctionHouse auctionHouse;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<Auction> notToBeGCd = new ArrayList<Auction>();

    public SniperLauncher(SnipersTableModel snipers, AuctionHouse auctionHouse) {
        this.snipers = snipers;
        this.auctionHouse = auctionHouse;
    }

    @Override
    public void joinAuction(String itemId) {
        snipers.addSniper(SniperSnapshot.joining(itemId));
        Auction auction = auctionHouse.auctionFor(itemId);
        notToBeGCd.add(auction);
        auction.addAuctionEventListener(new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId));
        auction.join();
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
