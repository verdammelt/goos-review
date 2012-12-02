package auctionsniper;

import java.util.ArrayList;

public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final ArrayList<Auction> notToBeGCd = new ArrayList<Auction>();
    private final SniperCollector collector;

    public SniperLauncher(SniperCollector collector, AuctionHouse auctionHouse) {
        this.collector = collector;
        this.auctionHouse = auctionHouse;
    }

    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        notToBeGCd.add(auction);
        AuctionSniper sniper = new AuctionSniper(auction, itemId);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
