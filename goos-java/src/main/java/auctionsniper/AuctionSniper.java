package auctionsniper;

import auctionsniper.lib.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
    private SniperSnapshot snapshot;
    private final Item item;

    public AuctionSniper(Auction auction, Item item) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.itemId);
        this.item = item;
    }

    @Override
    public void auctionFailed() {
        snapshot = snapshot.failed();
        notifyChange();
    }

    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(
            int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                if (item.allowsBid(bid)) {
                    auction.bid(bid);
                    snapshot = snapshot.bidding(price, bid);
                } else {
                    snapshot = snapshot.losing(price);
                }
                break;
        }
        notifyChange();
    }

    private void notifyChange() {
        listeners.announce().sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener listener) {
        listeners.addListener(listener);
    }
}
